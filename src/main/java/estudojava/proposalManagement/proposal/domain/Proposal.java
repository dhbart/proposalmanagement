package estudojava.proposalManagement.proposal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class Proposal {
    private ProposalId id;
    private String title;
    Optional<String> description;
    private Owner owner;

    public Proposal(String title, Optional<String> description, Owner owner) {
        this.id = new ProposalId();
        this.title = title;
        this.description = description;
        this.owner = owner;
    }
}
