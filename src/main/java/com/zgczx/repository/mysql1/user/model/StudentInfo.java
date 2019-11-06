package com.zgczx.repository.mysql1.user.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @author aml
 * @date 2019/9/10 15:40
 */
@Entity
@Data
@Table(name = "student_info", schema = "score_ananlysis_dev", catalog = "")
public class StudentInfo {
    private int id;
    private String userId;
    private String studentMachineCard;
    private String studentNumber;
    private String studentName;
    private String schoolName;
    private String schoolId;
    private String gradeId;
    private String gradeName;
    private String classId;
    private String className;
    private String cardNum;
    private String province;
    private String address;
    private String birthdate;
    private String sex;
    private String phone;
    private String image;
    private Long createUser;
    private Timestamp createTime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "student_machine_card")
    public String getStudentMachineCard() {
        return studentMachineCard;
    }

    public void setStudentMachineCard(String studentMachineCard) {
        this.studentMachineCard = studentMachineCard;
    }

    @Basic
    @Column(name = "student_number")
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Basic
    @Column(name = "student_name")
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Basic
    @Column(name = "school_name")
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Basic
    @Column(name = "school_id")
    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    @Basic
    @Column(name = "grade_id")
    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    @Basic
    @Column(name = "grade_name")
    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    @Basic
    @Column(name = "class_id")
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    @Basic
    @Column(name = "class_name")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Basic
    @Column(name = "card_num")
    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    @Basic
    @Column(name = "province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "birthdate")
    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    @Basic
    @Column(name = "sex")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "image")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Basic
    @Column(name = "create_user")
    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentInfo that = (StudentInfo) o;
        return id == that.id &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(studentMachineCard, that.studentMachineCard) &&
                Objects.equals(studentNumber, that.studentNumber) &&
                Objects.equals(studentName, that.studentName) &&
                Objects.equals(schoolName, that.schoolName) &&
                Objects.equals(schoolId, that.schoolId) &&
                Objects.equals(gradeId, that.gradeId) &&
                Objects.equals(gradeName, that.gradeName) &&
                Objects.equals(classId, that.classId) &&
                Objects.equals(className, that.className) &&
                Objects.equals(cardNum, that.cardNum) &&
                Objects.equals(province, that.province) &&
                Objects.equals(address, that.address) &&
                Objects.equals(birthdate, that.birthdate) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(image, that.image) &&
                Objects.equals(createUser, that.createUser) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, studentMachineCard, studentNumber, studentName, schoolName, schoolId, gradeId, gradeName, classId, className, cardNum, province, address, birthdate, sex, phone, image, createUser, createTime);
    }
}
