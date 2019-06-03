package com.internal.mediasdk;

public class HangupReason {
    public static final int NORMAL_HANGUP = 0;
    /** register rs error*/
    public static final int USER_OFFLINE = 1;
    /** reject */
    public static final int REJECT = 2;
    /** busy*/
    public static final int BUSYING = 3;
    /** timeout*/
    public static final int NO_ANSWER = 4;
    /**http rest api timeout*/
    public static final int NOT_REGISTERUSER = 5;
    /**http rest api */
    public static final int NETWORK_ERROR = 6;
    /** caller setup call timeout */
    public static final int TIMEOUT_HANGUP = 7;
    /**unknown error*/
    public static final int CALL_FAIL = 8;
    /** cancel */
    public static final int USER_CANCEL = 9;
    /** no used for sdk*/
    public static final int GSMCALL_INTERRUPT = 10;
    /** rca*/
    public static final int ALREADY_ANSWERED = 14;
    /** */
    public static final int CALL_FINISH = 15;
    /** callee at GSM call*/
    public static final int USER_INGSMCALL = 16;
    /** no used for sdk**/
    public static final int HOLD_BY_GSM = 17;
    /** no used for sdk**/
    public static final int HANGUP_RETRY_TIMEOUT = 18;
    /** no used for sdk**/
    public static final int HANGUP_CALL_REJECT  =19;
}
