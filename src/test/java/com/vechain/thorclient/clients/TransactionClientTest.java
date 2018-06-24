package com.vechain.thorclient.clients;


import com.alibaba.fastjson.JSON;
import com.vechain.thorclient.base.BaseTest;
import com.vechain.thorclient.core.model.blockchain.Receipt;
import com.vechain.thorclient.core.model.blockchain.Transaction;
import com.vechain.thorclient.core.model.blockchain.TransferResult;
import com.vechain.thorclient.core.model.clients.*;
import com.vechain.thorclient.core.model.clients.base.AbstractToken;
import com.vechain.thorclient.utils.CryptoUtils;
import com.vechain.thorclient.utils.RawTransactionFactory;
import com.vechain.thorclient.utils.crypto.ECKeyPair;

import java.io.IOException;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TransactionClientTest extends BaseTest {

    static String hexId = "0xa82d1dd26bae27a04fe1567658963b870232d2c9c73222b70f3227c7b086ae8a";
    static String addUserTxId = "0x5485ab3aaf5ff9160a33566de7d727aa5eb9e49b041edbb72b5e7877ada9b168";
    static String removeUserTxId = "0x3bec812d64615584414595e050bb52be9c0807cb1c05dc2ea9286a1e7c6a4da0";
    static String setUserPlanTxId = "0xbce3d27c6e4fc70ab0e46c48ee773ebd0d7a2d35e4668f39e2c6108b8e7c6219";


    @Test
    public void testGetTransaction() throws IOException {

        Transaction transaction = TransactionClient.getTransaction(hexId, false, null);
        logger.info("Transaction:" + JSON.toJSONString(transaction));
        Assert.assertNotNull(transaction);
    }

    @Test
    public void testGetTransactionRaw() throws IOException {
        Transaction transaction = TransactionClient.getTransaction(hexId, true, null);
        logger.info("Transaction:" + JSON.toJSONString(transaction));
        Assert.assertNotNull(transaction);
        Assert.assertNotNull(transaction.getRaw());
    }

    @Test
    public void testGetTransactionReceipt() throws IOException {
        Receipt receipt = TransactionClient.getTransactionReceipt(removeUserTxId, null);
        logger.info("Receipt:" + JSON.toJSONString(receipt));
        Assert.assertNotNull(receipt);
    }

    @Test
    public void testSendVTHOTransaction() throws IOException{
        byte chainTag = BlockchainClient.getChainTag();
        byte[] blockRef = BlockClient.getBlock( null ).blockRef().toByteArray();
        Amount amount = Amount.createFromToken( ERC20Token.VTHO );
        amount.setDecimalAmount( "11.12" );
        ToClause clause = ERC20Contract.buildTranferToClause( ERC20Token.VTHO,
                Address.fromHexString("VXc71ADC46c5891a8963Ea5A5eeAF578E0A2959779"),
                amount);
        RawTransaction rawTransaction =RawTransactionFactory.getInstance().createRawTransaction( chainTag, blockRef, 720, 80000, (byte)0x01, CryptoUtils.generateTxNonce(), clause);

        TransferResult result = TransactionClient.signThenTransfer( rawTransaction, ECKeyPair.create( privateKey ) );
        logger.info( "transfer vethor result:" + JSON.toJSONString( result ) );
        Assert.assertNotNull( result );
    }

    @Test
    public void testSendVETTransaction() throws IOException{
        byte chainTag = BlockchainClient.getChainTag();
        byte[] blockRef = BlockClient.getBlock( null ).blockRef().toByteArray();
        Amount amount = Amount.createFromToken( AbstractToken.VET );
        amount.setDecimalAmount( "21.12" );
        ToClause clause = TransactionClient.buildVETToClause(
                Address.fromHexString( "VXc71ADC46c5891a8963Ea5A5eeAF578E0A2959779" ),
                amount,
                ToData.ZERO );
        RawTransaction rawTransaction =RawTransactionFactory.getInstance().createRawTransaction( chainTag, blockRef, 720, 21000, (byte)0x01, CryptoUtils.generateTxNonce(), clause);
        TransferResult result = TransactionClient.signThenTransfer( rawTransaction, ECKeyPair.create( privateKey ) );
        logger.info( "transfer vet result:" + JSON.toJSONString( result ) );
        Assert.assertNotNull( result );
    }

}
