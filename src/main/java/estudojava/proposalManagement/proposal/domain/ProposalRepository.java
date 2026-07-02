package estudojava.proposalManagement.proposal.domain;

import java.util.List;

public interface ProposalRepository {
    List<Proposal> findAll();
    Proposal save(Proposal proposal);
    List<Proposal> findAllByOwnerId(OwnerId ownerId);
}
