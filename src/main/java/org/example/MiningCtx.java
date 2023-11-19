package org.example;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MiningCtx {
	private volatile Integer              extranonce2_size;
	private volatile String               extranonce1;
	private volatile String               sub_details;
	private volatile String               job_id;
	private volatile String               prevhash;
	private volatile String               coinb1;
	private volatile String               coinb2;
	private volatile List<String>         merkle_branch;
	private volatile String               version;
	private volatile String               nbits;
	private volatile String               ntime;
	private volatile Boolean              clean_jobs;
	private volatile String               updatedPrevHash;
	public final     Map<Integer, Double> nHeightDiff = new ConcurrentHashMap<>();

	public Map<Integer, Double> getnHeightDiff() {
		return nHeightDiff;
	}

	public Integer getExtranonce2_size() {
		return extranonce2_size;
	}

	public void setExtranonce2_size(Integer extranonce2_size) {
		this.extranonce2_size = extranonce2_size;
	}

	public String getExtranonce1() {
		return extranonce1;
	}

	public void setExtranonce1(String extranonce1) {
		this.extranonce1 = extranonce1;
	}

	public String getSub_details() {
		return sub_details;
	}

	public void setSub_details(String sub_details) {
		this.sub_details = sub_details;
	}

	public String getJob_id() {
		return job_id;
	}

	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}

	public String getPrevhash() {
		return prevhash;
	}

	public void setPrevhash(String prevhash) {
		this.prevhash = prevhash;
	}

	public String getCoinb1() {
		return coinb1;
	}

	public void setCoinb1(String coinb1) {
		this.coinb1 = coinb1;
	}

	public String getCoinb2() {
		return coinb2;
	}

	public void setCoinb2(String coinb2) {
		this.coinb2 = coinb2;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNbits() {
		return nbits;
	}

	public void setNbits(String nbits) {
		this.nbits = nbits;
	}

	public String getNtime() {
		return ntime;
	}

	public List<String> getMerkle_branch() {
		return merkle_branch;
	}

	public void setNtime(String ntime) {
		this.ntime = ntime;
	}

	public void setMerkle_branch(List<String> merkle_branch) {
		this.merkle_branch = merkle_branch;
	}

	public Boolean getClean_jobs() {
		return clean_jobs;
	}

	public void setClean_jobs(Boolean clean_jobs) {
		this.clean_jobs = clean_jobs;
	}

	public String getUpdatedPrevHash() {
		return updatedPrevHash;
	}

	public void setUpdatedPrevHash(String updatedPrevHash) {
		this.updatedPrevHash = updatedPrevHash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof MiningCtx))
			return false;
		MiningCtx miningCtx = (MiningCtx) o;
		return Objects.equals(getExtranonce2_size(), miningCtx.getExtranonce2_size()) && Objects.equals(getExtranonce1(), miningCtx.getExtranonce1()) && Objects.equals(getSub_details(), miningCtx.getSub_details()) && Objects.equals(getJob_id(), miningCtx.getJob_id()) && Objects.equals(getPrevhash(), miningCtx.getPrevhash()) && Objects.equals(getCoinb1(), miningCtx.getCoinb1()) && Objects.equals(getCoinb2(), miningCtx.getCoinb2()) && Objects.equals(merkle_branch, miningCtx.merkle_branch) && Objects.equals(getVersion(), miningCtx.getVersion()) && Objects.equals(getNbits(), miningCtx.getNbits()) && Objects.equals(getNtime(), miningCtx.getNtime()) && Objects.equals(getClean_jobs(), miningCtx.getClean_jobs()) && Objects.equals(getUpdatedPrevHash(), miningCtx.getUpdatedPrevHash());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getExtranonce2_size(), getExtranonce1(), getSub_details(), getJob_id(), getPrevhash(), getCoinb1(), getCoinb2(), merkle_branch, getVersion(), getNbits(), getNtime(), getClean_jobs(), getUpdatedPrevHash());
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MiningCtx{");
		sb.append("extranonce2_size=").append(extranonce2_size);
		sb.append(", extranonce1='").append(extranonce1).append('\'');
		sb.append(", sub_details='").append(sub_details).append('\'');
		sb.append(", job_id='").append(job_id).append('\'');
		sb.append(", prevhash='").append(prevhash).append('\'');
		sb.append(", coinb1='").append(coinb1).append('\'');
		sb.append(", coinb2='").append(coinb2).append('\'');
		sb.append(", merkle_branch=").append(merkle_branch);
		sb.append(", version='").append(version).append('\'');
		sb.append(", nbits='").append(nbits).append('\'');
		sb.append(", ntime='").append(ntime).append('\'');
		sb.append(", clean_jobs=").append(clean_jobs);
		sb.append(", updatedPrevHash='").append(updatedPrevHash).append('\'');
		sb.append(", nHeightDiff=").append(nHeightDiff);
		sb.append('}');
		return sb.toString();
	}
}
