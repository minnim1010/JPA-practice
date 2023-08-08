package jpa.entity;

public enum OrderStatus {
    ORDER {
        @Override
        public String toString(){
            return "ORDER";
        }
    }, CANCEL{
        @Override
        public String toString(){
            return "CANCEL";
        }
    }
}
