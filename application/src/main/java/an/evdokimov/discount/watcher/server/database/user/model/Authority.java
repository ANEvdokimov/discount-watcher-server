package an.evdokimov.discount.watcher.server.database.user.model;

import lombok.Getter;

public enum Authority {
    FULL_ACCESS("full_access");

    @Getter
    private final String authority;

    Authority(String authority) {
        this.authority = authority;
    }
}
