package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

@Data
public class Transactions {

	@NotNull
	@NotEmpty
	private final String sourceAccountId;

	@NotNull
	@NotEmpty
	private final String destinationAccountId;

	@NotNull
	@Min(value = 1, message = "Transaction amount must be greater than 0.")
	private BigDecimal amount;

	@JsonCreator
	public Transactions(@JsonProperty("sourceAccountId") String sourceAccountId,
			@JsonProperty("destinationAccountId") String destinationAccountId,
			@JsonProperty("amount") BigDecimal amount) {
		this.sourceAccountId = sourceAccountId;
		this.destinationAccountId = destinationAccountId;
		this.amount = amount;
	}

	@Override
	public String toString() {
		return new StringBuffer().append("Transaction{ Account Id From = '").append(this.sourceAccountId)
				.append("\' | Account Id To = '").append(this.destinationAccountId).append("\' | Transfered Amount = ")
				.append(this.amount).append(" }").toString();
	}
}
