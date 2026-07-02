package estudojava.proposalManagement.proposal.infrastructure.persistence.http.request;

import estudojava.proposalManagement.proposal.application.input.CreateProposalInput;

import java.util.Optional;

public record CreateProposalRequest(String title, Optional<String> description) {

    public CreateProposalInput toInput() {
        return new CreateProposalInput(title, description);
    }
}