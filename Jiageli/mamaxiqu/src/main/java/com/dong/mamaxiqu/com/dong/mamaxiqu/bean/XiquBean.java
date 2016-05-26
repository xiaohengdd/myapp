package com.dong.mamaxiqu.com.dong.mamaxiqu.bean;

import java.util.List;

/**
 * Created by home on 2016/5/25.
 */
public class XiquBean {

    /**
     * _id : 574322ec47945d1445cb42a7
     * url : <iframe height=498 width=510 isAutoPlay=true src="http://player.youku.com/embed/XNzU0Nzg4NTU2" frameborder=0 allowfullscreen></iframe>
     * name : 朝阳沟
     * quyiid : 2
     * __v : 0
     */

    private List<XiqulistBean> xiqulist;

    public List<XiqulistBean> getXiqulist() {
        return xiqulist;
    }

    public void setXiqulist(List<XiqulistBean> xiqulist) {
        this.xiqulist = xiqulist;
    }

    public static class XiqulistBean {
        private String _id;
        private String url;
        private String name;
        private String quyiid;
        private int __v;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQuyiid() {
            return quyiid;
        }

        public void setQuyiid(String quyiid) {
            this.quyiid = quyiid;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }
    }
}
