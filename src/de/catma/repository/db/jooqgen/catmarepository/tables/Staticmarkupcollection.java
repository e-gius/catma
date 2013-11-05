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
public class Staticmarkupcollection extends org.jooq.impl.TableImpl<org.jooq.Record> {

	private static final long serialVersionUID = -2103453387;

	/**
	 * The singleton instance of <code>CatmaRepository.staticmarkupcollection</code>
	 */
	public static final de.catma.repository.db.jooqgen.catmarepository.tables.Staticmarkupcollection STATICMARKUPCOLLECTION = new de.catma.repository.db.jooqgen.catmarepository.tables.Staticmarkupcollection();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.Record> getRecordType() {
		return org.jooq.Record.class;
	}

	/**
	 * The column <code>CatmaRepository.staticmarkupcollection.staticMarkupCollectionID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> STATICMARKUPCOLLECTIONID = createField("staticMarkupCollectionID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>CatmaRepository.staticmarkupcollection.name</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this);

	/**
	 * The column <code>CatmaRepository.staticmarkupcollection.sourceDocumentID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> SOURCEDOCUMENTID = createField("sourceDocumentID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * Create a <code>CatmaRepository.staticmarkupcollection</code> table reference
	 */
	public Staticmarkupcollection() {
		super("staticmarkupcollection", de.catma.repository.db.jooqgen.catmarepository.Catmarepository.CATMAREPOSITORY);
	}

	/**
	 * Create an aliased <code>CatmaRepository.staticmarkupcollection</code> table reference
	 */
	public Staticmarkupcollection(java.lang.String alias) {
		super(alias, de.catma.repository.db.jooqgen.catmarepository.Catmarepository.CATMAREPOSITORY, de.catma.repository.db.jooqgen.catmarepository.tables.Staticmarkupcollection.STATICMARKUPCOLLECTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.jooq.Record, java.lang.Integer> getIdentity() {
		return de.catma.repository.db.jooqgen.catmarepository.Keys.IDENTITY_STATICMARKUPCOLLECTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.jooq.Record> getPrimaryKey() {
		return de.catma.repository.db.jooqgen.catmarepository.Keys.KEY_STATICMARKUPCOLLECTION_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.jooq.Record>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.jooq.Record>>asList(de.catma.repository.db.jooqgen.catmarepository.Keys.KEY_STATICMARKUPCOLLECTION_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<org.jooq.Record, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.jooq.Record, ?>>asList(de.catma.repository.db.jooqgen.catmarepository.Keys.FK_STATICMC_SOURCEDOCUMENTID00);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public de.catma.repository.db.jooqgen.catmarepository.tables.Staticmarkupcollection as(java.lang.String alias) {
		return new de.catma.repository.db.jooqgen.catmarepository.tables.Staticmarkupcollection(alias);
	}
}