package com.lc.im;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

/**
 * created by lvchao 2023/5/16
 * describe:
 */
public class OssManager {
    private OSSClient oss;

    //LTAI5tBJxzZqmxPEAm83vjFy
    //627WdVgrPzV8CKFVBfoUhTsEkHaEvT
    public void init(Context context) {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = "STS.NTLPGe9jbktChbRVLWdZqBpJM";
        String accessKeySecret = "9rqpvjT64bbf17CrHjPJSds7b5R3dHGXqacqHgmAtTHh";
        // 从STS服务获取的安全令牌（SecurityToken）。
        String securityToken = "CAIS8AF1q6Ft5B2yfSjIr5f5G/3R1LVD3LaoakTjskwCaNVdrbXhrzz2IHpMenRhA+Aatf41mW9Z7/calq93VZRFTEucgQKAQX4Oo22beIPkl5Gfz95t0e+IewW6Dxr8w7WhAYHQR8/cffGAck3NkjQJr5LxaTSlWS7OU/TL8+kFCO4aRQ6ldzFLKc5LLw950q8gOGDWKOymP2yB4AOSLjIx4Vcs0jMgsf7jmpHBt0WCtjCglL9J/baWC4O/csxhMK14V9qIx+FsfsLDqnUKs0YXpf0m0Pceo2qY74jMW0My7xiDNO3P6cFiNxNpiAz282TCcJIagAFP2mZgJRLq3baZvgN2wJducmI8svKLbdQbrepfDmMXv6sYBTsxLtaCjT22GPf2QFstCeX0BrMikIbxBnxRLZPnpPq7oRnEV+ycYOhIOyYBT+qb+BDVXLQkLQ+h69m6y+ug3jYpzNJRRMYGc+XeoSmPIaedwrGs196ciaAiCndsSw==";

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        // 创建OSSClient实例。
        oss = new OSSClient(context.getApplicationContext(), endpoint, credentialProvider);
    }

    public OSSClient getOss() {
        return oss;
    }

    public void upload(String filePath, String fileName) {
        OSSClient oss = getOss();
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest("wonderfulbucket", fileName, filePath);

        // 异步上传时可以设置进度回调。
        put.setProgressCallback((request, currentSize, totalSize) -> Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize));

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag() + "--" + result.getServerCallbackReturnBody());
                Log.d("RequestId", result.getRequestId());

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

}
