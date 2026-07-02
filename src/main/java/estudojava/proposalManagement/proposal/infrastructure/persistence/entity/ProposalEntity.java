package estudojava.proposalManagement.proposal.infrastructure.persistence.entity;

import estudojava.proposalManagement.proposal.domain.Owner;
import estudojava.proposalManagement.proposal.domain.OwnerId;
import estudojava.proposalManagement.proposal.domain.Proposal;
import estudojava.proposalManagement.proposal.domain.ProposalId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String ownerName;

    public static ProposalEntity fromProposal(Proposal proposal) {
        return new ProposalEntity(
                proposal.getId().id(),
                proposal.getTitle(),
                proposal.getDescription().orElse(null),
                proposal.getOwner().id().id(),
                proposal.getOwner().name()
        );
    }

    public  Proposal toDomain() {
        return new Proposal(
                new ProposalId(),
                this.title,
                Optional.ofNullable(this.description),
                new Owner(new OwnerId(this.ownerId), this.ownerName)
        );
    }


}
