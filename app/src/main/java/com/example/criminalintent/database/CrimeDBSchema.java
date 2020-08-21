package com.example.criminalintent.database;

public class CrimeDBSchema {
    // data/data/com.example.criminalintent/database/CrimeDB.db
    public static final String NAME = "CrimeDB.db";
    public static final int VERSION = 1;

    public static final class CrimeTable {
        public static final String NAME = "CrimeTable";

        public static final class COLS {
            public static final String ID = "id";
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
