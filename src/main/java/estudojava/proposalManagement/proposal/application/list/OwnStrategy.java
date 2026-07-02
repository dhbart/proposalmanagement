package estudojava.proposalManagement.proposal.application.list;

import estudojava.proposalManagement.proposal.domain.OwnerId;
import estudojava.proposalManagement.proposal.domain.Proposal;
import estudojava.proposalManagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnStrategy implements Strategy {
    private final ProposalRepository proposalRepository;

    public OwnStrategy(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @Override
    public List<Proposal> getProposals(OwnerId ownerId) {
        return proposalRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public AccessScope getScope() {
        return AccessScope.OWN;
    }
}
