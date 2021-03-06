package com.nic.thooimaikaavalar.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TooimaiKavalar";
    private static final int DATABASE_VERSION = 3;

    public static final String DISTRICT_TABLE_NAME = "DistrictTable";
    public static final String BLOCK_TABLE_NAME = " BlockTable";


    public static final String SCHEME_TABLE_NAME = "SchemeList";
    public static final String FINANCIAL_YEAR_TABLE_NAME = "FinancialYear";
    public static final String WORK_STAGE_TABLE = "work_type_stage_link";
    public static final String ADDITIONAL_WORK_STAGE_TABLE = "addditional_work_stages";
    public static final String WORK_LIST_TABLE_BASED_ON_FINYEAR_VIlLAGE = "WorkList_based_on_finYear_village";
    public static final String ADDITIONAL_WORK_LIST = "additional_work_list";
    public static final String SAVE_IMAGE = "save_image";
    public static final String SAVE_ADDITIONAL_DATA = "save_additional_data";
    public static final String GET_ADDITIONAL_DETAILS_LIST = "get_additional_details_list";

    ////  ************** Need **********
    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String SWM_CAPACITY_OF_MCC_IN_TONNES_LIST = "swm_capacity_of_mcc_in_tones";
    public static final String SWM_NO_OF_THOOIMAI_KAAVALARS_LIST = "swm_no_of_thooimai_kaavalars";
    public static final String SWM_PHOTOGRAPHS_OF_MCC_COMPONENTS_LIST = "swm_photographs_of_mcc_components";
    public static final String SWM_WATER_SUPPLY_AVAILABILITY_LIST = "swm_water_supply_availability";
    public static final String BASIC_DETAILS_OF_MCC_SAVE = "basic_details_of_micro_composting_save";
    public static final String BASIC_DETAILS_OF_MCC_SAVE_SERVER = "basic_details_of_micro_composting_save_server";
    public static final String THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_SERVER = "thooimai_kaavalars_detail_of_micro_composting_save_server";
    public static final String THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_LOCAL = "thooimai_kaavalars_detail_of_micro_composting_save_local";
    public static final String COMPOST_TUB_IMAGE_TABLE = "compost_tub_image_table";
    public static final String WASTE_COLLECTED_SAVE_TABLE = "waste_collected_save_table";

    ////SWM TABLES
    public static final String SWM_MASTER_DETAILS_TABLE = "swm_master_details_table";
    public static final String SWM_MASTER_DETAILS_SERVER_TABLE = "swm_master_details_server_table";
    public static final String SWM_ASSET_TYPE = "swm_asset_type";
    public static final String SWM_ASSET_DETAILS_TABLE = "swm_asset_details_table";
    public static final String SWM_ASSET_PHOTOS_TABLE = "swm_asset_photos_table";
    public static final String SWM_WASTE_DUMP_PHOTOS_DETAILS = "swm_waste_dump_photos_details";
    public static final String SWM_CARRIED_OUT_DETAILS = "swm_carried_out_details";
    public static final String SWM_CARRIED_OUT_PHOTOS_DETAILS = "swm_carried_out_photos_details";
    public static final String PWM_VILLAGE_TABLE_NAME = "pwm_villageTable";

    ////**************///////

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

        db.execSQL("CREATE TABLE " + HABITATION_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habitation_code TEXT," +
                "habitation_name_ta TEXT,"+
                "habitation_name TEXT)");


        db.execSQL("CREATE TABLE " + SWM_CAPACITY_OF_MCC_IN_TONNES_LIST + " ("
                + "id INTEGER," +
                "capacity_of_mcc_name TEXT)");

        db.execSQL("CREATE TABLE " + SWM_NO_OF_THOOIMAI_KAAVALARS_LIST + " ("
                + "id INTEGER," +
                "thooimai_kaavalars_name TEXT)");

        db.execSQL("CREATE TABLE " + SWM_PHOTOGRAPHS_OF_MCC_COMPONENTS_LIST + " ("
                + "id INTEGER," +
                "photographs_name_ta TEXT," +
                "photographs_name TEXT)");

        db.execSQL("CREATE TABLE " + SWM_WATER_SUPPLY_AVAILABILITY_LIST + " ("
                + "id INTEGER," +
                "water_supply_availability_name_ta TEXT," +
                "water_supply_availability_name TEXT)");

        db.execSQL("CREATE TABLE " + SWM_ASSET_TYPE + " ("
                + "swm_asset_type_id INTEGER," +
                "asset_type_name TEXT," +
                "asset_type_name_ta TEXT," +
                "no_of_photos TEXT," +
                "is_this_others TEXT)");

        db.execSQL("CREATE TABLE " + BASIC_DETAILS_OF_MCC_SAVE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                 "mcc_id TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "mcc_name TEXT," +
                "capacity_of_mcc_id TEXT,"+
                "water_supply_availability_id TEXT,"+
                "water_supply_availability_other TEXT,"+
                "date_of_commencement TEXT,"+
                "image BLOB,"+
                "latitude TEXT,"+
                "longitude TEXT)");
        db.execSQL("CREATE TABLE " + BASIC_DETAILS_OF_MCC_SAVE_SERVER + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "mcc_id TEXT," +
                "mcc_name TEXT," +
                "dcode TEXT," +
                "dname TEXT," +
                "bcode TEXT," +
                "bname TEXT," +
                "pvcode TEXT," +
                "pvname TEXT," +
                "capacity_of_mcc_id TEXT,"+
                "capacity_of_mcc_name TEXT,"+
                "no_of_thooimai_kaavalars TEXT,"+
                "water_supply_availability_id TEXT,"+
                "water_supply_availability_name TEXT,"+
                "water_supply_availability_other TEXT,"+
                "date_of_commencement TEXT,"+
                "composting_center_image_available TEXT,"+
                "composting_center_image BLOB,"+
                "latitude TEXT,"+
                "longitude TEXT)");

        db.execSQL("CREATE TABLE " + THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_LOCAL + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "mcc_id TEXT," +
                "thooimai_kaavalar_id TEXT," +
                "name_of_the_thooimai_kaavalars TEXT," +
                "mobile_no TEXT," +
                "date_of_engagement TEXT," +
                "date_of_training_given TEXT)");
        db.execSQL("CREATE TABLE " + THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_SERVER + " ("
                +"mcc_id TEXT," +
                "dcode TEXT," +
                "dname TEXT," +
                "bcode TEXT," +
                "bname TEXT," +
                "pvcode TEXT," +
                "pvname TEXT," +
                "name_of_the_thooimai_kaavalars TEXT," +
                "mobile_no TEXT," +
                "thooimai_kaavalar_id TEXT," +
                "date_of_engagement TEXT," +
                "date_of_training_given TEXT)");

        db.execSQL("CREATE TABLE " + COMPOST_TUB_IMAGE_TABLE + " ("
                +"mcc_id TEXT," +
                "component_id TEXT," +
                "image blob," +
                "latitude TEXT," +
                "longitude TEXT," +
                "photograph_remark TEXT)");

        db.execSQL("CREATE TABLE " + WASTE_COLLECTED_SAVE_TABLE + " ("
                +"pvcode TEXT," +
                "pvname TEXT," +
                "mcc_id TEXT," +
                "mcc_name TEXT," +
                "households_waste TEXT," +
                "shops_waste TEXT," +
                "market_waste TEXT," +
                "hotels_waste TEXT," +
                "others_waste TEXT," +
                "tot_bio_waste_collected TEXT," +
                "tot_bio_waste_shredded TEXT," +
                "tot_bio_compost_produced TEXT," +
                "tot_bio_compost_sold TEXT," +
                "date_of_save TEXT)");



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


        ///new Need SWM Tables
        db.execSQL("CREATE TABLE " + SWM_MASTER_DETAILS_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "swm_infra_details_id TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "pvname TEXT," +
                "no_of_thooimai_kaavalars_allocated TEXT," +
                "no_of_thooimai_kaavalars_working TEXT,"+
                "whether_community_compost_pit_available_in_panchayat TEXT,"+
                "whether_vermi_compost_pit_available_in_panchayat TEXT,"+
                "is_plastic_connected_to_waste_management_unit TEXT,"+
                "any_integrated_nuesery_devlp_near_swm_facility TEXT)");

        db.execSQL("CREATE TABLE " + SWM_MASTER_DETAILS_SERVER_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "swm_infra_details_id TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "pvname TEXT," +
                "no_of_thooimai_kaavalars_allocated TEXT," +
                "no_of_thooimai_kaavalars_working TEXT,"+
                "whether_community_compost_pit_available_in_panchayat TEXT,"+
                "whether_vermi_compost_pit_available_in_panchayat TEXT,"+
                "is_plastic_connected_to_waste_management_unit TEXT,"+
                "is_there_any_waste_dump TEXT,"+
                "any_integrated_nuesery_devlp_near_swm_facility TEXT)");

        db.execSQL("CREATE TABLE " + SWM_ASSET_DETAILS_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "swm_infra_details_id TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "pvname TEXT," +
                "others_name TEXT," +
                "swm_asset_type_id TEXT," +
                "asset_type_name TEXT," +
                "is_functional TEXT)");

        db.execSQL("CREATE TABLE " + SWM_ASSET_PHOTOS_TABLE + " ("
                + "id INTEGER ,"+
                "swm_infra_details_id TEXT,"+
                "swm_asset_type_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "pvname TEXT," +
                "lattitude TEXT," +
                "longtitude TEXT," +
                "image BLOB)");

        db.execSQL("CREATE TABLE " + SWM_WASTE_DUMP_PHOTOS_DETAILS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "swm_infra_details_id TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "is_there_any_waste_dump TEXT," +
                "lattitude TEXT," +
                "longtitude TEXT," +
                "image BLOB)");

        db.execSQL("CREATE TABLE " + SWM_CARRIED_OUT_DETAILS + " ("
                + "carried_out_details_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "swm_infra_details_id TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "date_entry_for TEXT," +
                "total_quantity_of_waste TEXT," +
                "quantity_of_bio_degradable_waste TEXT," +
                "total_quantity_of_compost_generated_from_community TEXT," +
                "total_quantity_of_compost_generated_from_vermi TEXT," +
                "quantity_of_compost_sold TEXT," +
                "amount_of_compostable_waste_sent_for_recycling_in_kg TEXT," +
                "amount_of_compostable_waste_sent_for_recycling_revenue_in_rs TEXT," +
                "amount_of_plastic_waste_sent_to_pwm_unit_in_kg TEXT," +
                "amount_of_plastic_waste_sent_to_pwm_unit_revenue_in_rs TEXT," +
                "where_the_attached_pwm_unit_is_located TEXT," +
                "amt_of_compostable_garbage_collected TEXT," +
                "receipt_file BLOB," +
                "total_revenue_generated TEXT)");
        db.execSQL("CREATE TABLE " + SWM_CARRIED_OUT_PHOTOS_DETAILS + " ("
                + "carried_out_details_id INTEGER ,"+
                "swm_infra_details_id TEXT,"+
                "swm_waste_dump_photos_id TEXT,"+
                "date_entry_for TEXT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "is_photo_of_waste_dump_after_action TEXT," +
                "after_taken_image_lat TEXT," +
                "after_taken_image_long TEXT," +
                "after_taken_image BLOB)");

        db.execSQL("CREATE TABLE " + PWM_VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname_ta TEXT," +
                "pvname TEXT)");



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
            db.execSQL("DROP TABLE IF EXISTS " + SWM_CAPACITY_OF_MCC_IN_TONNES_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_NO_OF_THOOIMAI_KAAVALARS_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_PHOTOGRAPHS_OF_MCC_COMPONENTS_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_WATER_SUPPLY_AVAILABILITY_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + BASIC_DETAILS_OF_MCC_SAVE);
            db.execSQL("DROP TABLE IF EXISTS " + BASIC_DETAILS_OF_MCC_SAVE_SERVER);
            db.execSQL("DROP TABLE IF EXISTS " + THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_SERVER);
            db.execSQL("DROP TABLE IF EXISTS " + THOOIMAI_KAAVALARS_DETAIL_OF_MCC_SAVE_LOCAL);
            db.execSQL("DROP TABLE IF EXISTS " + COMPOST_TUB_IMAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + WASTE_COLLECTED_SAVE_TABLE);

            ///new SWM Tables
            db.execSQL("DROP TABLE IF EXISTS " + SWM_MASTER_DETAILS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_MASTER_DETAILS_SERVER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_ASSET_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_ASSET_DETAILS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_ASSET_PHOTOS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_WASTE_DUMP_PHOTOS_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_CARRIED_OUT_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + SWM_CARRIED_OUT_PHOTOS_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + PWM_VILLAGE_TABLE_NAME);

            onCreate(db);
        }
    }


}
