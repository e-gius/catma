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
public class UnseparableCharsequence extends org.jooq.impl.TableImpl<org.jooq.Record> {

	private static final long serialVersionUID = -1063716050;

	/**
	 * The singleton instance of <code>CatmaRepository.unseparable_charsequence</code>
	 */
	public static final de.catma.repository.db.jooqgen.catmarepository.tables.UnseparableCharsequence UNSEPARABLE_CHARSEQUENCE = new de.catma.repository.db.jooqgen.catmarepository.tables.UnseparableCharsequence();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.jooq.Record> getRecordType() {
		return org.jooq.Record.class;
	}

	/**
	 * The column <code>CatmaRepository.unseparable_charsequence.uscID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> USCID = createField("uscID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * The column <code>CatmaRepository.unseparable_charsequence.charsequence</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.String> CHARSEQUENCE = createField("charsequence", org.jooq.impl.SQLDataType.VARCHAR.length(45), this);

	/**
	 * The column <code>CatmaRepository.unseparable_charsequence.sourceDocumentID</code>. 
	 */
	public final org.jooq.TableField<org.jooq.Record, java.lang.Integer> SOURCEDOCUMENTID = createField("sourceDocumentID", org.jooq.impl.SQLDataType.INTEGER, this);

	/**
	 * Create a <code>CatmaRepository.unseparable_charsequence</code> table reference
	 */
	public UnseparableCharsequence() {
		super("unseparable_charsequence", de.catma.repository.db.jooqgen.catmarepository.Catmarepository.CATMAREPOSITORY);
	}

	/**
	 * Create an aliased <code>CatmaRepository.unseparable_charsequence</code> table reference
	 */
	public UnseparableCharsequence(java.lang.String alias) {
		super(alias, de.catma.repository.db.jooqgen.catmarepository.Catmarepository.CATMAREPOSITORY, de.catma.repository.db.jooqgen.catmarepository.tables.UnseparableCharsequence.UNSEPARABLE_CHARSEQUENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<org.jooq.Record, java.lang.Integer> getIdentity() {
		return de.catma.repository.db.jooqgen.catmarepository.Keys.IDENTITY_UNSEPARABLE_CHARSEQUENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.jooq.Record> getPrimaryKey() {
		return de.catma.repository.db.jooqgen.catmarepository.Keys.KEY_UNSEPARABLE_CHARSEQUENCE_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.jooq.Record>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.jooq.Record>>asList(de.catma.repository.db.jooqgen.catmarepository.Keys.KEY_UNSEPARABLE_CHARSEQUENCE_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<org.jooq.Record, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.jooq.Record, ?>>asList(de.catma.repository.db.jooqgen.catmarepository.Keys.FK_USCS_SOURCEDOCUMENTID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public de.catma.repository.db.jooqgen.catmarepository.tables.UnseparableCharsequence as(java.lang.String alias) {
		return new de.catma.repository.db.jooqgen.catmarepository.tables.UnseparableCharsequence(alias);
	}
}
