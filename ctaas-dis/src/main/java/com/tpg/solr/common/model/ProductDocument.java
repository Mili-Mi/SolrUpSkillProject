package com.tpg.solr.common.model;

import org.apache.solr.client.solrj.beans.Field;


public class ProductDocument {

    @Field
    private String pid;
 
    @Field
    private String pdescription;
 
    @Field
    private String pname;
 
    @Field
    private int pquantity;
    @Field
    private float pprice;
    @Field
    private String pcategory;
    
    public ProductDocument() {
 
    }
 
    public static Builder getBuilder(Long pid, String pname, int pquantity, float  pprice,String pcategory ) {
        return new Builder(pid,pname, pquantity,pprice,pcategory );
    }
 
    //Getters are omitted
 
    public static class Builder {
        private ProductDocument build;
 
        public Builder(Long pid, String pname, int pquantity, float  pprice,String pcategory) {
            build = new ProductDocument();
            build.pid = pid.toString();
            build.pname = pname;
            build.pquantity = pquantity;
            build.pprice = pprice;
            build.pcategory = pcategory;
        }
 
        public Builder description(String pdescription) {
            build.pdescription = pdescription;
            return this;
        }
 
        public ProductDocument build() {
            return build;
        }
    }
}
