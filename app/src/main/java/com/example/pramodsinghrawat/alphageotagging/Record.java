package com.example.pramodsinghrawat.alphageotagging;

/**
 * Created by PramodSinghRawat on 16-04-2016.
 */
public class Record {
    private String _id;
    private String _fileName;
    private String _remark;
    private String _latLng;
    private String _addedOn;

    private String[] _idAry;
    private String[] _fileNameAry;
    private String[] _remarkAry;
    private String[] _latLngAry;
    private String[] _addedOnAry;

    public Record() {}

    public Record(String id,String fileName,String remark, String latLng,String addedOn) {
        this._id = id;
        this._fileName=fileName;
        this._remark = remark;
        this._latLng = latLng;
        this._addedOn = addedOn;
    }

    public Record(String fileName,String remark, String latLng,String addedOn) {
        this._fileName=fileName;
        this._remark = remark;
        this._latLng = latLng;
        this._addedOn = addedOn;
    }

    public Record(String fileName,String remark, String latLng) {
        this._fileName=fileName;
        this._remark = remark;
        this._latLng = latLng;
    }

    public String getId() {return this._id;}
    public String getFileName() {return this._fileName; }
    public String getRemark() {return this._remark; }
    public String getLatLng() {return this._latLng; }
    public String getAddedOn() { return this._addedOn; }

    public Record(String[] idAry,String[] fileNameAry, String[] remarkAry,String[] latLngAry, String[] addedOnAry) {
        this._idAry = idAry;
        this._fileNameAry = fileNameAry;
        this._remarkAry = remarkAry;
        this._latLngAry = latLngAry;
        this._addedOnAry = addedOnAry;
    }
}
