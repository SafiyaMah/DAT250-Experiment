package no.hvl.dat250.experiment1.infra;

// Event for pub/sub
public record PollEvent(String type, Long pollId, Long optionId, Long voterId) {
    // String type poll-created or vote
    // Long pollId,
    // Long optionId null for poll created
    // Long voterId required for vote

    public static PollEvent pollCreated(Long pollId) {
        return new PollEvent("poll-created", pollId, null, null);
    }
    public static PollEvent vote(Long pollId, Long optionId, Long voterId) {
        return new PollEvent("vote", pollId, optionId, voterId);
    }
}