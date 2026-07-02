package estudojava.proposalManagement.proposal.application.list;

import estudojava.proposalManagement.proposal.domain.Owner;
import estudojava.proposalManagement.proposal.domain.OwnerId;
import estudojava.proposalManagement.proposal.domain.Proposal;

import java.util.List;

public interface Strategy {
    List<Proposal> getProposals(OwnerId ownerId);
    AccessScope getScope();
}
