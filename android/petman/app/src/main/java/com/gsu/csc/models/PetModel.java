package com.gsu.csc.models;

import java.util.Date;

public class PetModel {

    private int id;
    private String name;
    private String sex;
    private String breed;
    private int owner_id;
    private Date dob;
    private String type;

    public PetModel(int id, String name, String sex, String breed, int owner_id, Date dob, String type) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.breed = breed;
        this.owner_id = owner_id;
        this.dob = dob;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public Date getDob() {
        return this.dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
