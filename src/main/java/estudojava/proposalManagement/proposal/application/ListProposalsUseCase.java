package estudojava.proposalManagement.proposal.application;

import estudojava.proposalManagement.proposal.application.list.AccessScope;
import estudojava.proposalManagement.proposal.application.list.Factory;
import estudojava.proposalManagement.proposal.application.output.ProposalOutput;
import estudojava.proposalManagement.proposal.domain.OwnerId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProposalsUseCase {
    private final Factory factory;

    public ListProposalsUseCase(Factory factory) {
        this.factory = factory;
    }

    public List<ProposalOutput> execute(AccessScope scope, OwnerId ownerId) {
        var proposals = factory.getStrategy(scope).getProposals(ownerId);
        return proposals.stream().map(ProposalOutput::from).toList();
    }
}
