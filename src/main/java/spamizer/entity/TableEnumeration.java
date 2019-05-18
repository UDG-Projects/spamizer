package main.java.spamizer.entity;

public class TableEnumeration {

    public enum Table {
        RESULTS("RESULT"),
        SPAM("SPAM"),
        HAM("HAM"),
        MESSAGE("MESSAGE")
        ;

        private final String table;

        /**
         * @param table
         */
        Table(final String table) {
            this.table = table;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return table;
        }
    }

}
