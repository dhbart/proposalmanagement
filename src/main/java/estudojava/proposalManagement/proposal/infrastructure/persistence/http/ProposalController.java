package estudojava.proposalManagement.proposal.infrastructure.persistence.http;

import estudojava.proposalManagement.auth.domain.UserRole;
import estudojava.proposalManagement.auth.infrastructure.persistency.entity.User;
import estudojava.proposalManagement.proposal.application.CreateProposalUseCase;
import estudojava.proposalManagement.proposal.application.ListProposalsUseCase;
import estudojava.proposalManagement.proposal.application.list.AccessScope;
import estudojava.proposalManagement.proposal.domain.Owner;
import estudojava.proposalManagement.proposal.domain.OwnerId;
import estudojava.proposalManagement.proposal.infrastructure.persistence.http.request.CreateProposalRequest;
import estudojava.proposalManagement.proposal.infrastructure.persistence.http.response.ProposalResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final CreateProposalUseCase createProposalUseCase;
    private final ListProposalsUseCase listProposalsUseCase;

    public ProposalController(
            CreateProposalUseCase createProposalUseCase,
            ListProposalsUseCase listProposalsUseCase) {

        this.createProposalUseCase = createProposalUseCase;
        this.listProposalsUseCase = listProposalsUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('INFLUENCER')")
    public ProposalResponse createProposal(
            @RequestBody CreateProposalRequest request,
            @AuthenticationPrincipal User user) {

        var owner = new Owner(
                new OwnerId(user.getId()),
                user.getUsername()
        );

        var output = this.createProposalUseCase.execute(request.toInput(), owner);

        return ProposalResponse.from(output);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('INFLUENCER', 'BRAND')")
    public List<ProposalResponse> findAllProposals(@AuthenticationPrincipal User user) {

        var accessScope = getAccessScope(user.getRole());
        var ownerId = new OwnerId(user.getId());

        return listProposalsUseCase.execute(accessScope, ownerId)
                .stream()
                .map(ProposalResponse::from)
                .toList();
    }

    private static AccessScope getAccessScope(UserRole role) {
        return switch (role) {
            case ROLE_INFLUENCER -> AccessScope.OWN;
            case ROLE_BRAND -> AccessScope.ALL;
        };
    }
}