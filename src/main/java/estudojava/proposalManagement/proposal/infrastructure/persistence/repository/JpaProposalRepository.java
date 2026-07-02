package estudojava.proposalManagement.proposal.infrastructure.persistence.repository;

import estudojava.proposalManagement.proposal.domain.OwnerId;
import estudojava.proposalManagement.proposal.domain.Proposal;
import estudojava.proposalManagement.proposal.domain.ProposalRepository;
import estudojava.proposalManagement.proposal.infrastructure.persistence.entity.ProposalEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
public class JpaProposalRepository implements ProposalRepository {
    private final ProposalEntityRepository repository;

    public JpaProposalRepository(ProposalEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Proposal> findAll() {
        var iteralble = repository.findAll();
        return StreamSupport
                .stream(iteralble.spliterator(), false)
                .map(ProposalEntity::toDomain)
                .toList();
    }

    @Override
    public Proposal save(Proposal proposal) {
        var saved = repository.save(ProposalEntity.fromProposal(proposal));
        return saved.toDomain() ;
    }

    @Override
    public List<Proposal> findAllByOwnerId(OwnerId ownerId) {
        return repository.findAllByOwnerId(ownerId.id()).stream().map(ProposalEntity::toDomain).toList();

    }
}
