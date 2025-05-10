package com.emerson.desafiovotacao.external;

public class CpfValidationResponse {
	
	private VoteEligibilityStatus status;

	public CpfValidationResponse(VoteEligibilityStatus status) {
		this.status = status;
	}

	public VoteEligibilityStatus getStatus() {
		return status;
	}

	public void setStatus(VoteEligibilityStatus status) {
		this.status = status;
	}
}
