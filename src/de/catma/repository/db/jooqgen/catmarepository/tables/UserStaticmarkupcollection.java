/**
 * This class is generated by jOOQ
 */
package de.catma.repository.db.jooqgen.catmarepository.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.1.0" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserStaticmarkupcollection extends org.jooq.impl.TableImpl<org.jooq.Record> {

	private static final long serialVersionUID = -1306224936;

	/**
	 * The singleton instance of <code>CatmaRepository.user_staticmarkupcollection</code>
	 */
	public static final de.catma.repository.db.jooqgen.catmarepository.tables.UserStaticmarkupcollection USER_STATICMARKUPCOLLECTION = new de.catma.repository.db.jooqgen.catmarepository.tables.UserStaticmarkupcollection();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.Record> getRecordType() {
		return org.jooq.Record.class;
	}

	/**
	 * The column <code>CatmaRepository.user_staticmarkupcollection.user_staticmarkupcollectionID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> USER_STATICMARKUPCOLLECTIONID = createField("user_staticmarkupcollectionID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>CatmaRepository.user_staticmarkupcollection.userID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> USERID = createField("userID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>CatmaRepository.user_staticmarkupcollection.staticMarkupCollectionID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> STATICMARKUPCOLLECTIONID = createField("staticMarkupCollectionID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>CatmaRepository.user_staticmarkupcollection.accessMode</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> ACCESSMODE = createField("accessMode", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>CatmaRepository.user_staticmarkupcollection.owner</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Byte> OWNER = createField("owner", org.jooq.impl.SQLDataType.TINYINT, this);

	/**
	 * Create a <code>CatmaRepository.user_staticmarkupcollection</code> table reference
	 */
	public UserStaticmarkupcollection() {
		super("user_staticmarkupcollection", de.catma.repository.db.jooqgen.catmarepository.Catmarepository.CATMAREPOSITORY);
	}

	/**
	 * Create an aliased <code>CatmaRepository.user_staticmarkupcollection</code> table reference
	 */
	public UserStaticmarkupcollection(java.lang.String alias) {
		super(alias, de.catma.repository.db.jooqgen.catmarepository.Catmarepository.CATMAREPOSITORY, de.catma.repository.db.jooqgen.catmarepository.tables.UserStaticmarkupcollection.USER_STATICMARKUPCOLLECTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.jooq.Record, java.lang.Integer> getIdentity() {
		return de.catma.repository.db.jooqgen.catmarepository.Keys.IDENTITY_USER_STATICMARKUPCOLLECTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.jooq.Record> getPrimaryKey() {
		return de.catma.repository.db.jooqgen.catmarepository.Keys.KEY_USER_STATICMARKUPCOLLECTION_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.jooq.Record>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.jooq.Record>>asList(de.catma.repository.db.jooqgen.catmarepository.Keys.KEY_USER_STATICMARKUPCOLLECTION_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<org.jooq.Record, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.jooq.Record, ?>>asList(de.catma.repository.db.jooqgen.catmarepository.Keys.FK_USERSMC_USERID, de.catma.repository.db.jooqgen.catmarepository.Keys.FK_USERSMC_STATICMARKUPCOLLECTIONID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public de.catma.repository.db.jooqgen.catmarepository.tables.UserStaticmarkupcollection as(java.lang.String alias) {
		return new de.catma.repository.db.jooqgen.catmarepository.tables.UserStaticmarkupcollection(alias);
	}
}
