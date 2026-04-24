package enums;



public enum TransactionType {

    PIX (1),
    BILLET (2),
    CREDIT (3);

    private final int type;

    TransactionType (int typeValue){
          type = typeValue;
    }


}
