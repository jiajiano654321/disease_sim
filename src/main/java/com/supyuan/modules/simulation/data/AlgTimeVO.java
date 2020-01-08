package com.supyuan.modules.simulation.data;

public class AlgTimeVO {
	private int id;
	private int n;
	private double det;
	private double gam;
	private double a;
	private double b;
	private double faul;
	private double b1;
	private double faul1;
	private String year;
	private int collectionId;
	
	public AlgTimeVO() {
		
	}

	public AlgTimeVO(int id, int n, double det, double gam, double a, double b, double faul, double b1, double faul1) {
		this.id = id;
		this.n = n;
		this.det = det;
		this.gam = gam;
		this.a = a;
		this.b = b;
		this.faul = faul;
		this.b1 = b1;
		this.faul1 = faul1;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public double getDet() {
		return det;
	}

	public void setDet(double det) {
		this.det = det;
	}

	public double getGam() {
		return gam;
	}

	public void setGam(double gam) {
		this.gam = gam;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getFaul() {
		return faul;
	}

	public void setFaul(double faul) {
		this.faul = faul;
	}

	public double getB1() {
		return b1;
	}

	public void setB1(double b1) {
		this.b1 = b1;
	}

	public double getFaul1() {
		return faul1;
	}

	public void setFaul1(double faul1) {
		this.faul1 = faul1;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

}
