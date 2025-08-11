package io.txlens.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Aspect
@Component
public class TxLensAspect {

    private final PlatformTransactionManager transactionManager;

    public TxLensAspect(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Around("@annotation(io.txlens.annotations.TxRead)")
    public Object handleReadOnly(ProceedingJoinPoint pjp) throws Throwable {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template.execute(status -> {
            try {
                return pjp.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Around("@annotation(io.txlens.annotations.TxWrite)")
    public Object handleWrite(ProceedingJoinPoint pjp) throws Throwable {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(false);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template.execute(status -> {
            try {
                return pjp.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}

