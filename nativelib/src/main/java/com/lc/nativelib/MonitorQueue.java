package com.lc.nativelib;

import com.lc.nativelib.model.MessageInfo;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * 提供一下几个能力
 * （1）存储MessageInfo
 * （2）提供最多存多少MessageInfo的能力，例如：100条，如果数量超限制则丢掉老的记录
 * （3）构建MessageInfo的能力
 * （4）提供MessageInfo的缓存，队列中老的MessageInfo不会直接丢掉，而是清理之后放入这个缓存中备用
 * （5）对外提供数据的能力
 */
public class MonitorQueue {
    private final ArrayDeque<MessageInfo> queue = new ArrayDeque<>();
    private final ArrayList<MessageInfo> cache = new ArrayList<>();
    private int count;
    private int queueSize;

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public MessageInfo createNewTail() {
        MessageInfo tail = obtainMessage();
        add(tail);
        return tail;
    }

    public MessageInfo tail() {
        MessageInfo tail = queue.peekLast();
        if (tail == null) tail = createNewTail();
        return tail;
    }

    /**
     * 注意：如果使用这个函数就没有使用cache的能力，因此建议使用createNewTail函数
     *
     * @param messageInfo
     */
    public void add(MessageInfo messageInfo) {
        if (count < queueSize) {
            count++;
        } else {
            MessageInfo m = queue.pop();
            m.recycle();
        }
        queue.add(messageInfo);
    }


    private MessageInfo obtainMessage() {
        if (cache.size() != 0) {
            return cache.remove(0);
        }
        return new MessageInfo();
    }

    public ArrayDeque<MessageInfo> getQueue() {
        return queue;
    }

    public void clearQueue() {
        count = 0;
        queue.clear();
//        close();
    }

    public void close() {
        cache.clear();
    }
}
