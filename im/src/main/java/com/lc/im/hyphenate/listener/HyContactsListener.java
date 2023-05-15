package com.lc.im.hyphenate.listener;

import java.util.List;

/**
 * created by lvchao 2023/5/12
 * describe:
 */
public interface HyContactsListener {
    public void getAllContractsSuccess(List<String> contracts);

    public void getAllContractsFail(int code, String reason);
}
