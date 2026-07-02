package estudojava.proposalManagement.proposal.domain;

import java.util.UUID;

public record ProposalId(UUID id) {
    public ProposalId() {
        this(UUID.randomUUID());
    }
}
