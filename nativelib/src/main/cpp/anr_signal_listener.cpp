#include <jni.h>
#include <string>
#include <android/log.h>
#include <pthread.h>
#include <dirent.h>
#include <unistd.h>
#include <syslog.h>
#include <cinttypes>
#include "Support.h"
#include "managed_jnienv.h"
#include <filesystem>
#include <dirent.h>
#include <pthread.h>
#include <cxxabi.h>
#include <unistd.h>
#include <syscall.h>
#include <cstdlib>

#include <optional>
#include <cinttypes>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <iosfwd>
//#include <string>
#include <fcntl.h>
//#include "Logging.h"



#define TAG "Hensen"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define SIGNAL_CATCHER_THREAD_NAME "Signal Catcher"
#define SIGNAL_CATCHER_THREAD_SIGBLK 0x1000
#define O_WRONLY 00000001
#define O_CREAT 00000100
#define O_TRUNC 00001000

static struct StacktraceJNI {
    jclass AnrDetective;
    jclass ThreadPriorityDetective;
    jclass TouchEventLagTracer;
    jmethodID AnrDetector_onANRDumped;
    jmethodID AnrDetector_onANRDumpTrace;
    jmethodID AnrDetector_onPrintTrace;

    jmethodID AnrDetector_onNativeBacktraceDumped;

    jmethodID ThreadPriorityDetective_onMainThreadPriorityModified;
    jmethodID ThreadPriorityDetective_onMainThreadTimerSlackModified;
    jmethodID ThreadPriorityDetective_pthreadKeyCallback;

    jmethodID TouchEventLagTracer_onTouchEvenLag;
    jmethodID TouchEventLagTracer_onTouchEvenLagDumpTrace;
} gJ;


namespace MatrixTracer {
    static const int NATIVE_DUMP_TIMEOUT = 2; // 2 seconds
    JNIEnv *envJ;
    jobject system_anr_observed;

    static void anrDumpCallback() {
        if (envJ == nullptr) {
            return;
        }
        LOGD("anrDumpCallback");
        jclass obj_class = envJ->GetObjectClass(system_anr_observed);

        jmethodID getName_method = envJ->GetMethodID(obj_class, "onANRDumped", "()V");

        envJ->CallVoidMethod(system_anr_observed, getName_method);

//        jclass anrClass = envJ->FindClass("com/lc/nativelib/NativeLib");
//        jmethodID getName_method = envJ->GetMethodID(anrClass, "onANRDumped", "()V");
//
//        envJ->CallVoidMethod(system_anr_observed, getName_method);
    }

//    static void nativeInitSignalAnrDetective(JNIEnv *env, jclass) {
//        emplace(anrDumpCallback);
//    }

//    static JNINativeMethod methods[] = {
//            {"anrDumpCallback", "()V;", reinterpret_cast<void *>(anrDumpCallback)}
//    };

    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
        LOGD("JNI_OnLoad");
        JniInvocation::init(vm);//注册一个jvm
        JNIEnv *env;
        if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK)
            return -1;
        //获取java的Class
        jclass anrClass = env->FindClass("com/lc/nativelib/NativeLib");
        if (!anrClass)
            return -1;
        //将环境设置为静态，怎么说呢？现在还不是很理解，举个例子哈
        //设置了之后下面的onANRDumped函数只能定义成static的否则报错
        gJ.AnrDetective = static_cast<jclass>(env->NewGlobalRef(anrClass));
        //对应好Java的方法
//        gJ.AnrDetector_onANRDumped =
//                env->GetStaticMethodID(anrClass, "onANRDumped", "()V");

//        if (env->RegisterNatives(
//                anrClass, methods, sizeof(methods) / sizeof((methods)[0])) < 0)
//            return -1;

        env->DeleteLocalRef(anrClass);

        return JNI_VERSION_1_6;
    }

    void signalHandler(int sig, siginfo_t *info, void *uc) {
        (void) sig;
        (void) info;
        (void) uc;

        if (sig == SIGQUIT) {
            LOGD("ANR 被触发了");
            //反馈到Java层
            anrDumpCallback();
        }
    }


    static int getSignalCatcherThreadId() {
        char taskDirPath[128];
        DIR *taskDir;
        long long sigblk;
        int signalCatcherTid = -1;
        int firstSignalCatcherTid = -1;

        snprintf(taskDirPath, sizeof(taskDirPath), "/proc/%d/task", getpid());
        if ((taskDir = opendir(taskDirPath)) == nullptr) {
            return -1;
        }
        struct dirent *dent;
        pid_t tid;
        while ((dent = readdir(taskDir)) != nullptr) {
            tid = atoi(dent->d_name);
            if (tid <= 0) {
                continue;
            }

            char threadName[1024];
            char commFilePath[1024];
            snprintf(commFilePath, sizeof(commFilePath), "/proc/%d/task/%d/comm", getpid(), tid);

            Support::readFileAsString(commFilePath, threadName, sizeof(threadName));

            if (strncmp(SIGNAL_CATCHER_THREAD_NAME, threadName,
                        sizeof(SIGNAL_CATCHER_THREAD_NAME) - 1) != 0) {
                continue;
            }

            if (firstSignalCatcherTid == -1) {
                firstSignalCatcherTid = tid;
            }

            sigblk = 0;
            char taskPath[128];
            snprintf(taskPath, sizeof(taskPath), "/proc/%d/status", tid);

            ScopedFileDescriptor fd(open(taskPath, O_RDONLY, 0));
            LineReader lr(fd.get());
            const char *line;
            size_t len;
            while (lr.getNextLine(&line, &len)) {
                if (1 == sscanf(line, "SigBlk: %" SCNx64, &sigblk)) {
                    break;
                }
                lr.popLine(len);
            }
            if (SIGNAL_CATCHER_THREAD_SIGBLK != sigblk) {
                continue;
            }
            signalCatcherTid = tid;
            break;
        }
        closedir(taskDir);

        if (signalCatcherTid == -1) {
            signalCatcherTid = firstSignalCatcherTid;
        }
        return signalCatcherTid;
    }


    extern "C" JNIEXPORT void JNICALL
    Java_com_lc_nativelib_NativeLib_anrMonitor(
            JNIEnv *env,
            jobject thiz,
            jobject observed) {
        LOGD("monitor start");
        envJ = env;
        system_anr_observed = observed;
        //步骤一 我们通过pthread_sigmask把SIGQUIT设置为UNBLOCK
        //因为由于Android默认把SIGQUIT设置成了BLOCKED，系统默认只会响应sigwait而不会进入到我们设置的handler方法中
        sigset_t sigset;
        sigemptyset(&sigset);
        sigaddset(&sigset, SIGQUIT);
        pthread_sigmask(SIG_UNBLOCK, &sigset, nullptr);
        //步骤二 建立了SignalHandler监听ANR
        struct sigaction sa;
        sa.sa_sigaction = signalHandler;
        sa.sa_flags = SA_ONSTACK | SA_SIGINFO | SA_RESTART;
        sigaction(SIGQUIT, &sa, nullptr);
        //步骤三 重新向Signal Catcher线程发送一个SIGQUIT
        int tid = getSignalCatcherThreadId(); //遍历/proc/[pid]目录，找到SignalCatcher线程的tid
        tgkill(getpid(), tid, SIGQUIT);
        LOGD("monitor end");
    }

}