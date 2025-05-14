package com.staypick.staypick_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "accommodations")
@Data
public class Accommodation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String address;
  private String tel;
  private Double lat;
  private Double lng;

  @Column(name = "room_type")
  private String type;

  private String checkin;
  private String checkout;
  private String refund;

  private Boolean hasPark;
  private Boolean hasCooking;
  private Boolean hasPickup;
  private Boolean hasRestaurant;
  private Boolean hasSauna;
  private Boolean hasBarbecue;
  private Boolean hasFitness;
  private Boolean hasPc;
  private Boolean hasShower;
}

