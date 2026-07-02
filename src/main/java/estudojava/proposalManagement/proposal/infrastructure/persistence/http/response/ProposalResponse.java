package estudojava.proposalManagement.proposal.infrastructure.persistence.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import estudojava.proposalManagement.proposal.application.output.ProposalOutput;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProposalResponse(
        String id,
        String title,
        String description,
        OwnerResponse owner) {

    public record OwnerResponse(String id, String name) {
    }

    public static ProposalResponse from(ProposalOutput output) {
        return new ProposalResponse(
                output.id(),
                output.title(),
                output.description().orElse(null),
                new OwnerResponse(output.ownerId(), output.ownerName())
        );
    }
}