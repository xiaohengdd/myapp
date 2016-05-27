package com.cac.machehui.client.entity;

import java.util.List;

public class FoundListItem extends BaseEntity {

	private List<Found> carcommunityarticlelist;

	private FindTop carheadvo;

	public FindTop getCarheadvo() {
		return carheadvo;
	}

	public void setCarheadvo(FindTop carheadvo) {
		this.carheadvo = carheadvo;
	}

	public List<Found> getCarcommunityarticlelist() {
		return carcommunityarticlelist;
	}

	public void setCarcommunityarticlelist(List<Found> carcommunityarticlelist) {
		this.carcommunityarticlelist = carcommunityarticlelist;
	}

}
