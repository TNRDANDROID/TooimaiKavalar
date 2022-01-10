package com.nic.RealTimeMonitoringSystem.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RealTimeMonitoringSystem";
    private static final int DATABASE_VERSION = 1;

    public static final String DISTRICT_TABLE_NAME = "DistrictTable";
    public static final String BLOCK_TABLE_NAME = " BlockTable";
    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String SCHEME_TABLE_NAME = "SchemeList";
    public static final String FINANCIAL_YEAR_TABLE_NAME = "FinancialYear";
    public static final String WORK_STAGE_TABLE = "work_type_stage_link";
    public static final String ADDITIONAL_WORK_STAGE_TABLE = "addditional_work_stages";
    public static final String WORK_LIST_TABLE_BASED_ON_FINYEAR_VIlLAGE = "WorkList_based_on_finYear_village";
    public static final String ADDITIONAL_WORK_LIST = "additional_work_list";
    public static final String SAVE_IMAGE = "save_image";
    public static final String SAVE_ADDITIONAL_DATA = "save_additional_data";
    public static final String GET_ADDITIONAL_DETAILS_LIST = "get_additional_details_list";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DISTRICT_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "dname TEXT)");

        db.execSQL("CREATE TABLE " + BLOCK_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "bname TEXT)");

        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname TEXT)");


        db.execSQL("CREATE TABLE " + SCHEME_TABLE_NAME + " ("
                + "scheme_name TEXT," +
                "fin_year  TEXT," +
                "additional_data_status  TEXT," +
                "scheme_seq_id INTEGER)");

        db.execSQL("CREATE TABLE " + WORK_STAGE_TABLE + " ("
                + "work_group_id  INTEGER," +
                "work_type_id  INTEGER," +
                "work_stage_order  INTEGER," +
                "work_stage_code  INTEGER," +
                "additional_data_status  TEXT," +
                "work_stage_name TEXT)");

        db.execSQL("CREATE TABLE " + ADDITIONAL_WORK_STAGE_TABLE + " ("
                + "work_type_code  INTEGER," +
                "work_stage_order  INTEGER," +
                "work_stage_code  INTEGER," +
                "work_stage_name TEXT," +
                "cd_type_flag TEXT)");

        db.execSQL("CREATE TABLE " + FINANCIAL_YEAR_TABLE_NAME + " ("
                + "fin_year TEXT)");

        db.execSQL("CREATE TABLE " + WORK_LIST_TABLE_BASED_ON_FINYEAR_VIlLAGE + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "work_id INTEGER," +
                "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "schemegrp_name  TEXT," +
                "scheme_name  TEXT," +
                "fin_year  TEXT," +
                "agency_name  TEXT," +
                "wrkgrpname  TEXT," +
                "work_name  TEXT," +
                "work_group_id  INTEGER," +
                "work_type  INTEGER," +
                "mworkid  INTEGER," +
                "current_stage_of_work INTEGER," +
                "as_value INTEGER," +
                "amount_spent_sofar INTEGER," +
                "stage_name TEXT," +
                "hai_beneficiary_name TEXT," +
                "hai_beneficiary_fhname TEXT," +
                "worktypname TEXT," +
                "yn_completed TEXT," +
                "cd_prot_work_yn TEXT," +
                "state_code INTEGER," +
                "dname TEXT," +
                "bname TEXT," +
                "pvname TEXT," +
                "community_name TEXT," +
                "gender_text TEXT," +
                "upd_date TEXT,"+
                "image_available TEXT)");

        db.execSQL("CREATE TABLE " + ADDITIONAL_WORK_LIST + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "scheme_id  INTEGER," +
                "fin_year  TEXT," +
                "work_id  INTEGER," +
                "work_group_id  INTEGER," +
                "roadname  TEXT," +
                "cd_work_no  INTEGER," +
                "current_stage_of_work INTEGER," +
                "cd_type_id INTEGER," +
                "work_type_flag_le  TEXT," +
                "cd_code  INTEGER," +
                "cd_name  TEXT," +
                "chainage_meter  TEXT," +
                "work_stage_name TEXT,"+
                "image_available TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_IMAGE + " ("
                + "work_id INTEGER," +
                "type_of_work TEXT," +
                "work_group_id  INTEGER," +
                "work_type_id  INTEGER," +
                "cd_work_no  INTEGER," +
                "work_type_flag_le  TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "work_stage_code TEXT," +
                "work_stage_name TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "images blob," +
                "remark TEXT," +
                "length TEXT," +
                "width TEXT," +
                "height TEXT," +
                "server_flag  INTEGER DEFAULT 0," +
                "created_date TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_ADDITIONAL_DATA + " ("
                + "work_id INTEGER," +
                "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "type_of_work TEXT," +
                "work_group_id  INTEGER," +
                "work_type_id  INTEGER," +
                "length_water_storage TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "observation TEXT," +
                "no_trenches_per_worksite TEXT," +
                "no_units TEXT," +
                "length TEXT," +
                "height TEXT," +
                "width TEXT," +
                "depth TEXT," +
                "no_units_per_waterbody TEXT," +
                "area_covered TEXT," +
                "no_plantation_per_site TEXT," +
                "structure_sump_available_status TEXT," +
                "individual_community TEXT," +
                "length_channel TEXT)");
        db.execSQL("CREATE TABLE " + GET_ADDITIONAL_DETAILS_LIST + " ("
                + "work_id INTEGER," +
                "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "type_of_work TEXT," +
                "work_group_id  INTEGER," +
                "work_type_id  INTEGER," +
                "length_water_storage TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "observation TEXT," +
                "no_trenches_per_worksite TEXT," +
                "no_units TEXT," +
                "length TEXT," +
                "height TEXT," +
                "width TEXT," +
                "depth TEXT," +
                "no_units_per_waterbody TEXT," +
                "area_covered TEXT," +
                "no_plantation_per_site TEXT," +
                "structure_sump_available_status TEXT," +
                "individual_community TEXT," +
                "length_channel TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + DISTRICT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SCHEME_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_STAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FINANCIAL_YEAR_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_LIST_TABLE_BASED_ON_FINYEAR_VIlLAGE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_ADDITIONAL_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + GET_ADDITIONAL_DETAILS_LIST);

            onCreate(db);
        }
    }


}
