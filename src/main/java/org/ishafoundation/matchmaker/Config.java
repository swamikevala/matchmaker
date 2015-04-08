package org.ishafoundation.matchmaker;

import java.util.Map;

import org.ishafoundation.matchmaker.Metric;
import org.ishafoundation.matchmaker.scoring.ScoringMethod;
import org.ishafoundation.matchmaker.blocking.BlockingIndex;
import org.ishafoundation.matchmaker.api.IndexAPI;
import org.ishafoundation.matchmaker.api.DataSourceAPI;


public class Config {

	private Map<String, Metric> metricMap;
	private ScoringMethod scoringMethod;
	private Map<String, BlockingIndex> biMap;
	private IndexAPI indexAPI;
	private DataSourceAPI dataSourceAPI;

	//Getters
	
	public Map<String, Metric> getMetricMap() {
		return metricMap;
	}
	
	public ScoringMethod getScoringMethod() {
		return scoringMethod;
	}
	
	public Map<String, BlockingIndex> getBIMap() {
		return biMap;
	}
	
	public IndexAPI getIndexAPI() {
		return indexAPI;
	}
	
	public DataSourceAPI getDataSourceAPI() {
		return dataSourceAPI;
	}
	
	//Setters
	
	public void setMetricMap(Map<String, Metric> metricMap) {
		this.metricMap = metricMap;
	}
	
	public void setScoringMethod(ScoringMethod scoringMethod) {
		this.scoringMethod = scoringMethod;
	}
	
	public void setBIMap(Map<String, BlockingIndex> biMap) {
		this.biMap = biMap;
	}
	
	public void setIndexAPI(IndexAPI indexAPI) {
		this.indexAPI = indexAPI;
	}
	
	public void setDataSourceAPI(DataSourceAPI dataSourceAPI) {
		this.dataSourceAPI = dataSourceAPI;
	}
		
}