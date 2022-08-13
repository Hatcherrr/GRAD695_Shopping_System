package com.mercury.SpringBootRESTDemo.beans;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="MSI_ORDER")
public class Order {

	@Id
	@SequenceGenerator(name="msi_order_seq_gen", sequenceName="MSI_ORDER_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="msi_order_seq_gen")
	private int id;
	
	@Column
	private Date purchase_date;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
	private List<OrderProduct> purchases;

	public Order() {
		super();
	}

	public Order(int id, Date purchase_date, List<OrderProduct> purchases) {
		super();
		this.id = id;
		this.purchase_date = purchase_date;
		this.purchases = purchases;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getPurchase_date() {
		return purchase_date;
	}

	public void setPurchase_date(Date purchase_date) {
		this.purchase_date = purchase_date;
	}

	public List<OrderProduct> getPurchases() {
		return purchases;
	}

	public void setPurchases(List<OrderProduct> purchases) {
		this.purchases = purchases;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", purchase_date=" + purchase_date + ", purchases=" + purchases + "]";
	}
}
