package com.zgczx.dataobject.score;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/19 15:22
 */
@Entity
@Table(name = "subject_full_score", schema = "score_ananlysis_dev", catalog = "")
public class SubjectFullScore {
    private long id;
    private String schemaname;
    private Long yuwen;
    private Long shuxue;
    private Long yingyu;
    private Long wuli;
    private Long huaxue;
    private Long shengwu;
    private Long zhengzhi;
    private Long dili;
    private Long lishi;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "schemaname")
    public String getSchemaname() {
        return schemaname;
    }

    public void setSchemaname(String schemaname) {
        this.schemaname = schemaname;
    }

    @Basic
    @Column(name = "yuwen")
    public Long getYuwen() {
        return yuwen;
    }

    public void setYuwen(Long yuwen) {
        this.yuwen = yuwen;
    }

    @Basic
    @Column(name = "shuxue")
    public Long getShuxue() {
        return shuxue;
    }

    public void setShuxue(Long shuxue) {
        this.shuxue = shuxue;
    }

    @Basic
    @Column(name = "yingyu")
    public Long getYingyu() {
        return yingyu;
    }

    public void setYingyu(Long yingyu) {
        this.yingyu = yingyu;
    }

    @Basic
    @Column(name = "wuli")
    public Long getWuli() {
        return wuli;
    }

    public void setWuli(Long wuli) {
        this.wuli = wuli;
    }

    @Basic
    @Column(name = "huaxue")
    public Long getHuaxue() {
        return huaxue;
    }

    public void setHuaxue(Long huaxue) {
        this.huaxue = huaxue;
    }

    @Basic
    @Column(name = "shengwu")
    public Long getShengwu() {
        return shengwu;
    }

    public void setShengwu(Long shengwu) {
        this.shengwu = shengwu;
    }

    @Basic
    @Column(name = "zhengzhi")
    public Long getZhengzhi() {
        return zhengzhi;
    }

    public void setZhengzhi(Long zhengzhi) {
        this.zhengzhi = zhengzhi;
    }

    @Basic
    @Column(name = "dili")
    public Long getDili() {
        return dili;
    }

    public void setDili(Long dili) {
        this.dili = dili;
    }

    @Basic
    @Column(name = "lishi")
    public Long getLishi() {
        return lishi;
    }

    public void setLishi(Long lishi) {
        this.lishi = lishi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectFullScore that = (SubjectFullScore) o;
        return id == that.id &&
                Objects.equals(schemaname, that.schemaname) &&
                Objects.equals(yuwen, that.yuwen) &&
                Objects.equals(shuxue, that.shuxue) &&
                Objects.equals(yingyu, that.yingyu) &&
                Objects.equals(wuli, that.wuli) &&
                Objects.equals(huaxue, that.huaxue) &&
                Objects.equals(shengwu, that.shengwu) &&
                Objects.equals(zhengzhi, that.zhengzhi) &&
                Objects.equals(dili, that.dili) &&
                Objects.equals(lishi, that.lishi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, schemaname, yuwen, shuxue, yingyu, wuli, huaxue, shengwu, zhengzhi, dili, lishi);
    }
}
