package org.alfresco.repo.transfer.fsr;

import javax.transaction.UserTransaction;

import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class FileTransferReceiverMain
{
    private static Log logger = LogFactory.getLog(FileTransferReceiverMain.class);

    /**
     * @param args
     */
    public static void main(String[] args)
    {

        ApplicationContext context = new ClassPathXmlApplicationContext("fsr-bootstrap-context.xml");

        // RetryingTransactionHelper ts =(RetryingTransactionHelper)context.getBean("retryingTransactionHelper");
        FileTransferReceiverTransactionServiceImpl transactionService = (FileTransferReceiverTransactionServiceImpl) context
                .getBean("transactionService");
        TransferReceiver ftTransferReceiver = (TransferReceiver) context.getBean("transferReceiver");

        try
        {
            UserTransaction ut = transactionService.getUserTransaction();
            ut.begin();
            ftTransferReceiver.start("1234", false, ftTransferReceiver.getVersion());
            // QNameDAO qNameDAO = (QNameDAO)context.getBean("qnameDAO");

            // qNameDAO.getOrCreateNamespace("test2");
            // Pair<Long, String> pair = qNameDAO.getNamespace(1L);
            // System.out.println("Pair:" + pair.getSecond());

            ut.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ((ClassPathXmlApplicationContext) context).close();
        System.exit(0);
    }
}
