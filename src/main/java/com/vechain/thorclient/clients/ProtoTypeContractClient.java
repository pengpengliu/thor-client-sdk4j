package com.vechain.thorclient.clients;

import com.vechain.thorclient.core.model.blockchain.ContractCall;
import com.vechain.thorclient.core.model.blockchain.ContractCallResult;
import com.vechain.thorclient.core.model.blockchain.TransferResult;
import com.vechain.thorclient.core.model.clients.*;
import com.vechain.thorclient.core.model.clients.base.AbiDefinition;
import com.vechain.thorclient.core.model.exception.ClientArgumentException;
import com.vechain.thorclient.core.model.exception.ClientIOException;
import com.vechain.thorclient.utils.Prefix;
import com.vechain.thorclient.utils.crypto.ECKeyPair;


/**
 * ProtoType Contract is a native contract for user to do multiple parties payment(mpp).
 * <a href="https://github.com/vechain/thor/wiki/Prototype(CN)">Prototype</a>
 * <br>What is ProtoType for?</br>
 *
 * In common scenario, if a sender A want to make a transaction to receiver B,
 * the sender A need to pay the transaction fee(gas).
 *
 * However, for some realistic reason, the receiver A want to pay the transaction fee. Then the mpp can do such a thing.
 * The sender A can be a user of receiver B({@linkplain #addUser(Address[], Address[], int, byte, int, ECKeyPair)}),
 * then the receiver B can set user plan (credit and recovery) for all senders.
 * After all the things is done, then the sender A do transaction to receiver B, if the fee is less than the credit,
 * the ProtoType native contract is going to book fee(gas) from receiver B's account.
 *
 * <br>How to use ProtoType?</br>
 * First, you must be the master of the to address. By call {@link #setMasterAddress}, then you can query the master by
 * {@link #getMasterAddress(Address, Revision)}
 *
 * Second, you as a Master, you can set add user to user plan. This step is to set candidate, the one you want to give
 * credit. {@link #addUser(Address[], Address[], int, byte, int, ECKeyPair)}
 *
 * Third, set a user plan to receiver address for all users on the users list.
 * {@link #setUserPlans(Address[], Amount[], Amount[], int, byte, int, ECKeyPair)}
 *
 *
 */
public class ProtoTypeContractClient extends ContractClient{


    /**
     * Get a master address from receiver address.
     * @param receiver required {@link Address} receiver address, means transfer to address.
     * @param revision optional can be null {@link Revision} block revision.
     * @return Contract call result {@link Revision}
     * @throws ClientIOException
     */
    public static ContractCallResult getMasterAddress(Address receiver, Revision revision) throws ClientIOException {

        if(receiver == null){
            throw new IllegalArgumentException( "receiver is null" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "master" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ContractCall call = ProtoTypeContract.buildCall(abi, receiver.toHexString( Prefix.ZeroLowerX ));

        return callContract( call, ProtoTypeContract.ContractAddress, revision );
    }

    /**
     * Set master user
     * @param receivers {@link Address} arrays as receiver address.
     * @param newMasters {@link Address} arrays as new master address.
     * @param gas  contract invoker gas
     * @param gasCoef gas coef
     * @param expiration expiration, suggest 720
     * @param keyPair private key {@link ECKeyPair}
     * @return {@link TransferResult}
     * @throws ClientIOException network error.
     */
    public static TransferResult setMasterAddress(Address[] receivers, Address[] newMasters, int gas, byte gasCoef, int expiration , ECKeyPair keyPair) throws ClientIOException {
        if(receivers == null){
            throw ClientArgumentException.exception( "receivers is null" );
        }
        if(newMasters == null){
            throw ClientArgumentException.exception( "newMasters in null" );
        }
        if(receivers.length != newMasters.length){
            throw ClientArgumentException.exception( "receivers size must equal to newMasters size. " );
        }

        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "setMaster" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ToClause[] clauses = new ToClause[receivers.length];
        for(int index = 0; index < receivers.length; index ++){
            clauses[index] = ProtoTypeContract.buildToClause( ProtoTypeContract.ContractAddress, abi, receivers[index].toHexString( Prefix.ZeroLowerX ),newMasters[index].toHexString( Prefix.ZeroLowerX ));

        }
        return invokeContractMethod( clauses,gas,gasCoef,expiration, keyPair );
    }

    /**
     * Add user to user plan
     * @param receivers {@link Address} arrays.
     * @param users {@link Address} arrays.
     * @param gas  a contract invoker gas
     * @param gasCoef gas coef
     * @param expiration expiration, suggest it is 720.
     * @param keyPair {@link ECKeyPair}
     * @return {@link TransferResult}
     * @throws ClientIOException network error.
     */
    public static TransferResult addUser(Address[] receivers, Address[] users, int gas, byte gasCoef, int expiration , ECKeyPair keyPair) throws ClientIOException{
        if(receivers == null){
            throw ClientArgumentException.exception( "receivers is null" );
        }
        if(users == null){
            throw ClientArgumentException.exception( "users is null" );
        }
        if(receivers.length != users.length){
            throw ClientArgumentException.exception( "receivers size must equal to users size. " );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "addUser" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ToClause[] clauses = new ToClause[receivers.length];
        for(int index = 0; index < receivers.length; index ++){
            clauses[index] = ProtoTypeContract.buildToClause(
                    ProtoTypeContract.ContractAddress,
                    abi,
                    receivers[index].toHexString( Prefix.ZeroLowerX ),
                    users[index].toHexString( Prefix.ZeroLowerX ));

        }
        return invokeContractMethod( clauses,gas,gasCoef,expiration, keyPair );

    }

    /**
     * Remove user from receiver addresses.
     * @param receivers {@link Address} arrays.
     * @param users {@link Address} arrays.
     * @param gas  a contract invoker gas
     * @param gasCoef gas coef
     * @param expiration expiration, suggest it is 720.
     * @param keyPair {@link ECKeyPair}
     * @return {@link TransferResult}
     * @throws ClientIOException network error.
     */
    public  static TransferResult removeUsers(Address[] receivers, Address[] users, int gas, byte gasCoef, int expiration , ECKeyPair keyPair) throws ClientIOException {
        if(receivers == null){
            throw ClientArgumentException.exception( "receivers is null" );
        }
        if(users == null){
            throw ClientArgumentException.exception( "users is null" );
        }
        if(receivers.length != users.length){
            throw ClientArgumentException.exception( "receivers size must equal to users size. " );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "removeUser" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ToClause[] clauses = new ToClause[receivers.length];
        for(int index = 0; index < receivers.length; index ++){
            clauses[index] = ProtoTypeContract.buildToClause(
                    ProtoTypeContract.ContractAddress,
                    abi,
                    receivers[index].toHexString( Prefix.ZeroLowerX ),
                    users[index].toHexString( Prefix.ZeroLowerX ));

        }
        return invokeContractMethod( clauses,gas,gasCoef,expiration, keyPair );
    }

    /**
     * Set user plan
     * @param receivers {@link Address} array.
     * @param credits {@link Amount} array.
     * @param recoveryRates {@link Amount} array. thor per seconds.
     * @return {@link TransferResult}
     * @throws ClientIOException network error.
     */
    public static TransferResult setUserPlans( Address[] receivers,
                                      Amount[] credits, Amount[] recoveryRates,
                                      int gas, byte gasCoef, int expiration , ECKeyPair keyPair
    ) throws ClientIOException {

        if(receivers == null){
            throw ClientArgumentException.exception( "receivers is null" );
        }
        if(credits == null){
            throw ClientArgumentException.exception( "credits is null" );
        }
        if(recoveryRates == null){
            throw ClientArgumentException.exception( "recoveries is null" );
        }
        if(receivers.length != credits.length || receivers.length != recoveryRates.length){
            throw ClientArgumentException.exception( "users.length must equal to credits.length and equal to recoveries.length" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "setUserPlan" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ToClause[] clauses = new ToClause[receivers.length];
        for(int index = 0; index < receivers.length; index ++){
            clauses[index] = ProtoTypeContract.buildToClause(
                    ProtoTypeContract.ContractAddress,
                    abi,
                    receivers[index].toHexString( Prefix.ZeroLowerX ),
                    credits[index].toHexString(),
                    recoveryRates[index].toHexString());

        }
        return invokeContractMethod( clauses,gas,gasCoef,expiration, keyPair );
    }

    /**
     * Check if user address is a user of receiver address.
     * @param receiver required {@link Address} the receiver address.
     * @param user required {@link Address} the user address.
     * @param revision optional {@link Revision}.
     * @return {@link ContractCallResult}
     * @throws ClientIOException network error.
     */
    public static ContractCallResult isUser(Address receiver, Address user, Revision revision) throws ClientIOException{
        if(receiver == null){
            throw ClientArgumentException.exception( "receiver address is null" );
        }
        if(user == null){
            throw ClientArgumentException.exception( "user address is null" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "isUser" );
        ContractCall call = ProtoTypeContract.buildCall(abi,
                receiver.toHexString( Prefix.ZeroLowerX ) ,
                user.toHexString( Prefix.ZeroLowerX ));

        return callContract( call, ProtoTypeContract.ContractAddress,revision );
    }

    /**
     * Get user plan
     * @param receiver required {@link Address}
     * @param revision optional
     * @return {@link ContractCallResult}
     * @throws ClientIOException network error.
     */
    public static ContractCallResult getUserPlan(Address receiver, Revision revision) throws ClientIOException {
        if(receiver == null){
            throw ClientArgumentException.exception( "receiver is null" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "userPlan" );
        ContractCall call = ProtoTypeContract.buildCall(abi,
                receiver.toHexString( Prefix.ZeroLowerX ));

        return callContract( call, ProtoTypeContract.ContractAddress,revision );
    }


    /**
     * Get user credit from receiver address by some block revision.
     * @param receiver {@link Address} receiver address.
     * @param user {@link Address} user address.
     * @param revision {@link Revision} revision.
     * @return {@link ContractCallResult}
     * @throws ClientIOException network error.
     */
    public static ContractCallResult getUserCredit(Address receiver, Address user, Revision revision) throws ClientIOException{
        if(receiver == null){
            throw ClientArgumentException.exception( "receiver address is null" );
        }
        if(user == null){
            throw ClientArgumentException.exception( "user address is null" );
        }

        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "userCredit" );
        ContractCall call = ProtoTypeContract.buildCall(abi,
                receiver.toHexString( Prefix.ZeroLowerX ) ,
                user.toHexString( Prefix.ZeroLowerX ));

        return callContract( call, ProtoTypeContract.ContractAddress,revision );
    }


    /**
     * Sponsor the address or un-sponsor the address.
     * @param receivers required {@link Address} the targets address
     * @param yesOrNo required Boolean value.
     * @param gas required int gas
     * @param gasCoef required byte gas coef
     * @param expiration required int expiration
     * @param keyPair required {@link Address}
     * @return {@link TransferResult}
     * @throws ClientIOException network error.
     */
    public static TransferResult sponsor(Address[] receivers, Boolean yesOrNo,  int gas, byte gasCoef, int expiration , ECKeyPair keyPair)throws ClientIOException{
        if(receivers == null){
            throw ClientArgumentException.exception( "receivers is null" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "sponsor" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ToClause[] clauses = new ToClause[receivers.length];
        String yesOrNoHex = yesOrNo? "0x01": "0x00";
        for(int index = 0; index < receivers.length; index ++){
            clauses[index] = ProtoTypeContract.buildToClause(
                    ProtoTypeContract.ContractAddress,
                    abi,
                    receivers[index].toHexString( Prefix.ZeroLowerX ),
                    yesOrNoHex
                    );
        }
        return invokeContractMethod( clauses,gas,gasCoef,expiration, keyPair );
    }

    /**
     * As a master to select sponsors for receivers addresses.
     * @param receivers required {@link Address} array
     * @param sponsors required {@link Address} array
     * @param gas required int gas
     * @param gasCoef required byte gasCoef
     * @param expiration required int recommendation value is 720
     * @param keyPair required {@link ECKeyPair}
     * @return {@link TransferResult}
     * throw ClientIOException network error.
     */
    public static TransferResult selectSponsor(Address[] receivers, Address[] sponsors, int gas, byte gasCoef, int expiration , ECKeyPair keyPair)throws ClientIOException{
        if (receivers == null){
            throw ClientArgumentException.exception( "receivers is null" );
        }
        if (sponsors == null){
            throw ClientArgumentException.exception( "sponsors is null" );
        }

        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "selectSponsor" );
        if(abi == null){
            throw new IllegalArgumentException( "Can not find abi master method" );
        }
        ToClause[] clauses = new ToClause[receivers.length];

        for(int index = 0; index < receivers.length; index ++){
            clauses[index] = ProtoTypeContract.buildToClause(
                    ProtoTypeContract.ContractAddress,
                    abi,
                    receivers[index].toHexString( Prefix.ZeroLowerX ),
                    sponsors[index].toHexString( Prefix.ZeroLowerX )
            );
        }
        return invokeContractMethod( clauses,gas,gasCoef,expiration, keyPair );
    }

    /**
     * Get current sponsor from receiver address.
     * @param receiver {@link Address}
     * @param revision {@link Revision}
     * @return {@link ContractCallResult}
     * throw ClientIOException network error.
     */
    public static ContractCallResult getCurrentSponsor(Address receiver, Revision revision)throws ClientIOException{
        if(receiver == null){
            throw ClientArgumentException.exception( "receiver is null" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "currentSponsor" );
        ContractCall call = ProtoTypeContract.buildCall(abi,
                receiver.toHexString( Prefix.ZeroLowerX ));

        return callContract( call, ProtoTypeContract.ContractAddress,revision );
    }

    /**
     * Get boolean value if the sponsor address sponsored the receiver address.
     * @param receiver required {@link Address} receiver address.
     * @param sponsor required {@link Address} sponsor
     * @param revision optional {@link Revision} block revision
     * @return {@link ContractCallResult}
     * throw ClientIOException network error.
     */
    public static ContractCallResult isSponsor(Address receiver, Address sponsor, Revision revision)throws ClientIOException{
        if(receiver == null){
            throw ClientArgumentException.exception( "receiver is null" );
        }
        if(sponsor == null){
            throw ClientArgumentException.exception( "sponsor is null" );
        }
        AbiDefinition abi = ProtoTypeContract.defaultContract.findAbiDefinition( "isSponsor" );
        ContractCall call = ProtoTypeContract.buildCall(abi,
                receiver.toHexString( Prefix.ZeroLowerX ),
                sponsor.toHexString( Prefix.ZeroLowerX ));

        return callContract( call, ProtoTypeContract.ContractAddress,revision );
    }


}