package com.nic.RealTimeMonitoringSystem.model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class RealTimeMonitoringSystem {

    private String distictCode;
    private String districtName;

    private String blockCode;
    private String SchemeGroupName;
    private String SchemeName;
    private String Description;
    private String Latitude;
    private String selectedBlockCode;

    private String FinancialYear;
    private String AgencyName;
    private String WorkGroupNmae;
    private String WorkName;
    private String PvCode;
    private String PvName;

    private String blockName;
    private String Gender;
    private Integer CurrentStage;

    private String Name;
    private String BeneficiaryName;
    private String workGroupID;
    private String workTypeID;

    public String getBeneficiaryFatherName() {
        return BeneficiaryFatherName;
    }

    public void setBeneficiaryFatherName(String beneficiaryFatherName) {
        BeneficiaryFatherName = beneficiaryFatherName;
    }

    private String BeneficiaryFatherName;
    private String Community;
    private String IntialAmount;

    private String AmountSpendSoFar;
    private String LastVisitedDate;

    private Integer pendingActivity;
    public String workStageName;
    private String workStageCode;
    private String workStageOrder;
    private Integer schemeSequentialID;

    private Integer schemeGroupID;
    private Integer schemeID;
    private Integer asValue;
    private String stageName;
    private String workTypeName;
    private String ynCompleted;
    private String cdProtWorkYn;
    private Integer stateCode;

    private String roadName;
    private Integer cdWorkNo;
    private Integer cdCode;
    private String cdName;
    private String chainageMeter;
    private Integer cdTypeId;

    public Integer getCdTypeId() {
        return cdTypeId;
    }

    public void setCdTypeId(Integer cdTypeId) {
        this.cdTypeId = cdTypeId;
    }

    public String getWorkTypeFlagLe() {
        return workTypeFlagLe;
    }

    public void setWorkTypeFlagLe(String workTypeFlagLe) {
        this.workTypeFlagLe = workTypeFlagLe;
    }

    private String workTypeFlagLe;

    public String getChainageMeter() {
        return chainageMeter;
    }

    public void setChainageMeter(String chainageMeter) {
        this.chainageMeter = chainageMeter;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public Integer getCdWorkNo() {
        return cdWorkNo;
    }

    public void setCdWorkNo(Integer cdWorkNo) {
        this.cdWorkNo = cdWorkNo;
    }

    public Integer getCdCode() {
        return cdCode;
    }

    public void setCdCode(Integer cdCode) {
        this.cdCode = cdCode;
    }

    public String getCdName() {
        return cdName;
    }

    public void setCdName(String cdName) {
        this.cdName = cdName;
    }

    public String getYnCompleted() {
        return ynCompleted;
    }

    public void setYnCompleted(String ynCompleted) {
        this.ynCompleted = ynCompleted;
    }

    public String getCdProtWorkYn() {
        return cdProtWorkYn;
    }

    public void setCdProtWorkYn(String cdProtWorkYn) {
        this.cdProtWorkYn = cdProtWorkYn;
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }

    public Integer getCurrentStage() {
        return CurrentStage;
    }

    public void setCurrentStage(Integer currentStage) {
        CurrentStage = currentStage;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Integer getAsValue() {
        return asValue;
    }

    public void setAsValue(Integer asValue) {
        this.asValue = asValue;
    }

    public Integer getSchemeID() {
        return schemeID;
    }

    public void setSchemeID(Integer schemeID) {
        this.schemeID = schemeID;
    }

    public Integer getSchemeGroupID() {
        return schemeGroupID;
    }

    public void setSchemeGroupID(Integer schemeGroupID) {
        this.schemeGroupID = schemeGroupID;
    }

    public Integer getSchemeSequentialID() {
        return schemeSequentialID;
    }

    public void setSchemeSequentialID(Integer schemeSequentialID) {
        this.schemeSequentialID = schemeSequentialID;
    }

    private Integer noOfPhotos;

    public String getSchemeGroupName() {
        return SchemeGroupName;
    }

    public void setSchemeGroupName(String schemeGroupName) {
        SchemeGroupName = schemeGroupName;
    }

    public String getSchemeName() {
        return SchemeName;
    }

    public void setSchemeName(String schemeName) {
        SchemeName = schemeName;
    }

    public String getFinancialYear() {
        return FinancialYear;
    }

    public void setFinancialYear(String financialYear) {
        FinancialYear = financialYear;
    }

    public String getAgencyName() {
        return AgencyName;
    }

    public void setAgencyName(String agencyName) {
        AgencyName = agencyName;
    }

    public String getWorkGroupNmae() {
        return WorkGroupNmae;
    }

    public void setWorkGroupNmae(String workGroupNmae) {
        WorkGroupNmae = workGroupNmae;
    }

    public String getWorkName() {
        return WorkName;
    }

    public void setWorkName(String workName) {
        WorkName = workName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getBeneficiaryName() {
        return BeneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        BeneficiaryName = beneficiaryName;
    }

    public String getCommunity() {
        return Community;
    }

    public void setCommunity(String community) {
        Community = community;
    }

    public String getIntialAmount() {
        return IntialAmount;
    }

    public void setIntialAmount(String intialAmount) {
        IntialAmount = intialAmount;
    }

    public String getAmountSpendSoFar() {
        return AmountSpendSoFar;
    }

    public void setAmountSpendSoFar(String amountSpendSoFar) {
        AmountSpendSoFar = amountSpendSoFar;
    }

    public String getLastVisitedDate() {
        return LastVisitedDate;
    }

    public void setLastVisitedDate(String lastVisitedDate) {
        LastVisitedDate = lastVisitedDate;
    }

    public Integer getWorkId() {
        return WorkId;
    }

    public void setWorkId(Integer workId) {
        WorkId = workId;
    }

    private Integer WorkId;
    private String typeOfWork;
    private String imageRemark;
    private String dateTime;
    private String imageAvailable;
    private String createdDate;
    private String workTypeCode;
    private String length;
    private String width;
    private String height;
    private String additional_data_status;
    private String length_water_storage;
    private String observation;
    private String no_trenches_per_worksite;
    private String no_units;
    private String breadth;
    private String depth;
    private String no_units_per_waterbody;
    private String area_covered;
    private String no_plantation_per_site;
    private String structure_sump_available_status;
    private String individual_community;
    private String length_channel;


    public String getLength_water_storage() {
        return length_water_storage;
    }

    public RealTimeMonitoringSystem setLength_water_storage(String length_water_storage) {
        this.length_water_storage = length_water_storage;
        return this;
    }

    public String getObservation() {
        return observation;
    }

    public RealTimeMonitoringSystem setObservation(String observation) {
        this.observation = observation;
        return this;
    }

    public String getNo_trenches_per_worksite() {
        return no_trenches_per_worksite;
    }

    public RealTimeMonitoringSystem setNo_trenches_per_worksite(String no_trenches_per_worksite) {
        this.no_trenches_per_worksite = no_trenches_per_worksite;
        return this;
    }

    public String getNo_units() {
        return no_units;
    }

    public RealTimeMonitoringSystem setNo_units(String no_units) {
        this.no_units = no_units;
        return this;
    }

    public String getBreadth() {
        return breadth;
    }

    public RealTimeMonitoringSystem setBreadth(String breadth) {
        this.breadth = breadth;
        return this;
    }

    public String getDepth() {
        return depth;
    }

    public RealTimeMonitoringSystem setDepth(String depth) {
        this.depth = depth;
        return this;
    }

    public String getNo_units_per_waterbody() {
        return no_units_per_waterbody;
    }

    public RealTimeMonitoringSystem setNo_units_per_waterbody(String no_units_per_waterbody) {
        this.no_units_per_waterbody = no_units_per_waterbody;
        return this;
    }

    public String getArea_covered() {
        return area_covered;
    }

    public RealTimeMonitoringSystem setArea_covered(String area_covered) {
        this.area_covered = area_covered;
        return this;
    }

    public String getNo_plantation_per_site() {
        return no_plantation_per_site;
    }

    public RealTimeMonitoringSystem setNo_plantation_per_site(String no_plantation_per_site) {
        this.no_plantation_per_site = no_plantation_per_site;
        return this;
    }

    public String getStructure_sump_available_status() {
        return structure_sump_available_status;
    }

    public RealTimeMonitoringSystem setStructure_sump_available_status(String structure_sump_available_status) {
        this.structure_sump_available_status = structure_sump_available_status;
        return this;
    }

    public String getIndividual_community() {
        return individual_community;
    }

    public RealTimeMonitoringSystem setIndividual_community(String individual_community) {
        this.individual_community = individual_community;
        return this;
    }

    public String getLength_channel() {
        return length_channel;
    }

    public RealTimeMonitoringSystem setLength_channel(String length_channel) {
        this.length_channel = length_channel;
        return this;
    }

    public String getAdditional_data_status() {
        return additional_data_status;
    }

    public RealTimeMonitoringSystem setAdditional_data_status(String additional_data_status) {
        this.additional_data_status = additional_data_status;
        return this;
    }

    public String getLength() {
        return length;
    }

    public RealTimeMonitoringSystem setLength(String length) {
        this.length = length;
        return this;
    }

    public String getWidth() {
        return width;
    }

    public RealTimeMonitoringSystem setWidth(String width) {
        this.width = width;
        return this;
    }

    public String getHeight() {
        return height;
    }

    public RealTimeMonitoringSystem setHeight(String height) {
        this.height = height;
        return this;
    }

    public RealTimeMonitoringSystem setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getWorkTypeCode() {
        return workTypeCode;
    }

    public void setWorkTypeCode(String workTypeCode) {
        this.workTypeCode = workTypeCode;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTypeOfWork() {
        return typeOfWork;
    }

    public void setTypeOfWork(String typeOfWork) {
        this.typeOfWork = typeOfWork;
    }

    public String getImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(String imageAvailable) {
        this.imageAvailable = imageAvailable;
    }

    public String getImageRemark() {
        return imageRemark;
    }

    public void setImageRemark(String imageRemark) {
        this.imageRemark = imageRemark;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }



    public Integer getNoOfPhotos() {
        return noOfPhotos;
    }

    public void setNoOfPhotos(Integer noOfPhotos) {
        this.noOfPhotos = noOfPhotos;
    }


    public Integer getPendingActivity() {
        return pendingActivity;
    }

    public void setPendingActivity(Integer pendingActivity) {
        this.pendingActivity = pendingActivity;
    }




    public String getPvName() {
        return PvName;
    }

    public void setPvName(String pvName) {
        PvName = pvName;
    }



    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getSelectedBlockCode() {
        return selectedBlockCode;
    }

    public void setSelectedBlockCode(String selectedBlockCode) {
        this.selectedBlockCode = selectedBlockCode;
    }




    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    private String Longitude;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    private Bitmap Image;



    public String getPvCode() {
        return PvCode;
    }

    public void setPvCode(String pvCode) {
        this.PvCode = pvCode;
    }

    public void setWorkStageName(String workStageName) {
        this.workStageName = workStageName;
    }

    public String getWorkStageName() {
        return workStageName;
    }

    public void setWorkGroupID(String workGroupID) {
        this.workGroupID = workGroupID;
    }

    public String getWorkGroupID() {
        return workGroupID;
    }

    public void setWorkTypeID(String workTypeID) {
        this.workTypeID = workTypeID;
    }

    public String getWorkTypeID() {
        return workTypeID;
    }

    public void setWorkStageCode(String workStageCode) {
        this.workStageCode = workStageCode;
    }

    public String getWorkStageCode() {
        return workStageCode;
    }

    public void setWorkStageOrder(String workStageOrder) {
        this.workStageOrder = workStageOrder;
    }

    public String getWorkStageOrder() {
        return workStageOrder;
    }

    public Integer getId() {
        return id;
    }
    private Integer id;

}