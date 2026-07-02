package estudojava.proposalManagement.proposal.application.input;

import estudojava.proposalManagement.proposal.domain.Owner;
import estudojava.proposalManagement.proposal.domain.Proposal;

import java.util.Optional;

public record CreateProposalInput(String title, Optional<String> description) {
    public Proposal toDomain(Owner owner) {
        return new Proposal(title, description, owner);
    }
}
