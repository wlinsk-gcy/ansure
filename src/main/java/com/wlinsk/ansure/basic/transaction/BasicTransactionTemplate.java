package com.wlinsk.ansure.basic.transaction;

import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @Author: wlinsk
 * @Date: 2024/5/22
 */
@Slf4j
@Component
public class BasicTransactionTemplate {

    @Autowired
    private TransactionTemplate transactionTemplate;

    public <T> T execute(TransactionCallback<T> action) throws BasicException {
        T execute;
        try {
            execute  = transactionTemplate.execute(action);
        }catch (TransactionException e){
            log.error("平台事物异常 TransactionException:",e);
            throw new BasicException(SysCode.TRANSACTION_EXCEPTION);
        }catch (Error err) {
            log.error("平台事物异常 Error:",err);
            throw new BasicException(SysCode.TRANSACTION_EXCEPTION);
        }catch (Exception ex) {
            log.error("平台事物异常 Exception:",ex);
            throw new BasicException(SysCode.TRANSACTION_EXCEPTION);
        } catch (Throwable t) {
            log.error("平台事物异常 Throwable:",t);
            throw t;
        }
        return execute;
    }
}
