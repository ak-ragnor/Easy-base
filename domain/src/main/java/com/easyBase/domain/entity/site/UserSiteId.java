package com.easyBase.domain.entity.site;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * UserSiteId Composite Primary Key
 *
 * Represents the composite primary key for the UserSite mapping entity.
 * This class is required for JPA when using @IdClass annotation.
 *
 * @author Akhash R
 * @version 1.0
 * @since 1.0
 */
public class UserSiteId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * User ID component of the composite key
     */
    private Long user;

    /**
     * Site ID component of the composite key
     */
    private Long site;

    /**
     * Default constructor required by JPA
     */
    public UserSiteId() {
    }

    /**
     * Constructor with both key components
     *
     * @param user User ID
     * @param site Site ID
     */
    public UserSiteId(Long user, Long site) {
        this.user = user;
        this.site = site;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getSite() {
        return site;
    }

    public void setSite(Long site) {
        this.site = site;
    }

    // ===== EQUALS, HASHCODE, AND TOSTRING =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSiteId that)) return false;

        return Objects.equals(user, that.user) &&
                Objects.equals(site, that.site);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, site);
    }

    @Override
    public String toString() {
        return "UserSiteId{" +
                "user=" + user +
                ", site=" + site +
                '}';
    }
}