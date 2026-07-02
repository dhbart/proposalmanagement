package estudojava.proposalManagement.proposal.application;

import estudojava.proposalManagement.proposal.application.input.CreateProposalInput;
import estudojava.proposalManagement.proposal.application.output.ProposalOutput;
import estudojava.proposalManagement.proposal.domain.Owner;
import estudojava.proposalManagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateProposalUseCase {
    private final ProposalRepository repository;

    public CreateProposalUseCase(ProposalRepository repository) {
        this.repository = repository;
    }

    public ProposalOutput execute(CreateProposalInput input, Owner owner) {
        var proposal = input.toDomain(owner);
        var saved = repository.save(proposal);
        return ProposalOutput.from(saved);
    }
}
