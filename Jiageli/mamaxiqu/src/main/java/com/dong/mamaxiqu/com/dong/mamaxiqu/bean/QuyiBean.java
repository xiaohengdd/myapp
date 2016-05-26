package com.dong.mamaxiqu.com.dong.mamaxiqu.bean;

import java.util.List;

/**
 * Created by home on 2016/5/24.
 */
public class QuyiBean {


    /**
     * _id : 5742fac6f475445e1d3a21a3
     * quyiid : 1
     * sortname : 京剧
     */

    private List<QuyilistBean> quyilist;

    public List<QuyilistBean> getQuyilist() {
        return quyilist;
    }

    public void setQuyilist(List<QuyilistBean> quyilist) {
        this.quyilist = quyilist;
    }

    public static class QuyilistBean {
        private String _id;
        private String quyiid;
        private String sortname;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getQuyiid() {
            return quyiid;
        }

        public void setQuyiid(String quyiid) {
            this.quyiid = quyiid;
        }

        public String getSortname() {
            return sortname;
        }

        public void setSortname(String sortname) {
            this.sortname = sortname;
        }
    }
}
