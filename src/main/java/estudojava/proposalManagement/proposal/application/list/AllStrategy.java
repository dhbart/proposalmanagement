package estudojava.proposalManagement.proposal.application.list;

import estudojava.proposalManagement.proposal.domain.OwnerId;
import estudojava.proposalManagement.proposal.domain.Proposal;
import estudojava.proposalManagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AllStrategy implements  Strategy {
    private final ProposalRepository proposalRepository;

    public AllStrategy(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @Override
    public List<Proposal> getProposals(OwnerId ownerId) {
        return proposalRepository.findAll();
    }

    @Override
    public AccessScope getScope() {
        return AccessScope.ALL;
    }
}

