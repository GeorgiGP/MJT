package bg.sofia.uni.fmi.mjt.socialnetwork.post;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SocialFeedPost implements Post {
    private final String uniqueId;
    private final UserProfile author;
    private String content;
    private final LocalDateTime date;
    private final HashMap<ReactionType, Set<UserProfile>> reactions;
    private int totalReactionsCount;

    public SocialFeedPost(UserProfile author, String content) {
        this.uniqueId = UUID.randomUUID().toString();
        this.author = author;
        this.content = content;
        this.date = LocalDateTime.now();
        this.reactions = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialFeedPost that = (SocialFeedPost) o;
        return Objects.equals(uniqueId, that.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uniqueId);
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public UserProfile getAuthor() {
        return author;
    }

    @Override
    public LocalDateTime getPublishedOn() {
        return date;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean addReaction(UserProfile userProfile, ReactionType reactionType) {
        if (userProfile == null || reactionType == null) {
            throw new IllegalArgumentException("userProfile is null or reactionType is null");
        }
        boolean toReturn = !removeReaction(userProfile);
        if (toReturn) {
            totalReactionsCount++;
        }
        if (!reactions.containsKey(reactionType)) {
            reactions.put(reactionType, new HashSet<>());
        }
        reactions.get(reactionType).add(userProfile);
        return toReturn;
    }

    @Override
    public boolean removeReaction(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }
        for (ReactionType reactionType : reactions.keySet()) {
            if (!reactions.containsKey(reactionType)) {
                continue;
            }
            if (reactions.get(reactionType).remove(userProfile)) {
                if (reactions.get(reactionType).isEmpty()) {
                    reactions.remove(reactionType);
                }
                totalReactionsCount--;
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<ReactionType, Set<UserProfile>> getAllReactions() {
        return Collections.unmodifiableMap(reactions);
    }

    @Override
    public int getReactionCount(ReactionType reactionType) {
        if (reactionType == null) {
            throw new IllegalArgumentException("reactionType is null");
        }
        if (!reactions.containsKey(reactionType)) {
            return 0;
        }
        return reactions.get(reactionType).size();
    }

    @Override
    public int totalReactionsCount() {
        return totalReactionsCount;
    }
}
