package org.opennms.core.criteria.restrictions;

import java.util.List;

public class Restrictions {

	private static final Restriction[] EMPTY_RESTRICTION_ARRAY = new Restriction[0];

    public static NullRestriction isNull(final String attribute) {
		return new NullRestriction(attribute);
	}
	
	public static NotNullRestriction isNotNull(final String attribute) {
		return new NotNullRestriction(attribute);
	}

	public static EqRestriction id(final Integer id) {
		return eq("id", id);
	}

	public static EqRestriction eq(final String attribute, final Object comparator) {
		return new EqRestriction(attribute, comparator);
	}

    public static Restriction ne(final String attribute, final Object comparator) {
        return new NeRestriction(attribute, comparator);
    }

	public static GtRestriction gt(final String attribute, final Object comparator) {
		return new GtRestriction(attribute, comparator);
	}

	public static GeRestriction ge(final String attribute, final Object comparator) {
		return new GeRestriction(attribute, comparator);
	}

	public static LtRestriction lt(final String attribute, final Object comparator) {
		return new LtRestriction(attribute, comparator);
	}

	public static LeRestriction le(final String attribute, final Object comparator) {
		return new LeRestriction(attribute, comparator);
	}

	public static LikeRestriction like(final String attribute, final Object comparator) {
		return new LikeRestriction(attribute, comparator);
	}

	public static IlikeRestriction ilike(final String attribute, final Object comparator) {
		return new IlikeRestriction(attribute, comparator);
	}

    public static IplikeRestriction iplike(final String attribute, final Object comparator) {
        return new IplikeRestriction(attribute, comparator);
    }

    public static InRestriction in(final String attribute, final List<?> list) {
		return new InRestriction(attribute, list);
	}

	public static BetweenRestriction between(final String attribute, final Object begin, final Object end) {
		return new BetweenRestriction(attribute, begin, end);
	}

	public static NotRestriction not(final Restriction restriction) {
		return new NotRestriction(restriction);
	}

	public static AllRestriction and(final Restriction lhs, final Restriction rhs) {
		return new AllRestriction(lhs, rhs);
	}

	public static AnyRestriction or(final Restriction lhs, final Restriction rhs) {
		return new AnyRestriction(lhs, rhs);
	}

    public static AllRestriction all(final Restriction... restrictions) {
        return new AllRestriction(restrictions);
    }

    public static AllRestriction all(final List<Restriction> restrictions) {
        return new AllRestriction(restrictions.toArray(EMPTY_RESTRICTION_ARRAY));
    }

	public static AnyRestriction any(final Restriction... restrictions) {
		return new AnyRestriction(restrictions);
	}

    public static AnyRestriction any(final List<Restriction> restrictions) {
        return new AnyRestriction(restrictions.toArray(EMPTY_RESTRICTION_ARRAY));
    }

	public static AttributeRestriction sql(final String sql) {
		return new SqlRestriction(sql);
	}

}
