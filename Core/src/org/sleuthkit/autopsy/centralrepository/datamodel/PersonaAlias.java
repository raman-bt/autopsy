/*
 * Central Repository
 *
 * Copyright 2020 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.centralrepository.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.sleuthkit.datamodel.SleuthkitCase;

/**
 * This class abstracts an alias assigned to a Persona. A Persona may have
 * multiple aliases.
 *
 */
public class PersonaAlias {

    private static final String SELECT_QUERY_BASE
            = "SELECT pa.id, pa.persona_id, pa.alias, pa.justification, pa.confidence_id, pa.date_added, pa.examiner_id, e.login_name, e.display_name "
            + "FROM persona_alias as pa "
            + "INNER JOIN examiners as e ON e.id = pa.examiner_id ";

    private final long id;
    private final long personaId;
    private final String alias;
    private final String justification;
    private final Persona.Confidence confidence;
    private final long dateAdded;
    private final CentralRepoExaminer examiner;

    public long getId() {
        return id;
    }

    public long getPersonaId() {
        return personaId;
    }

    public String getAlias() {
        return alias;
    }

    public String getJustification() {
        return justification;
    }

    public Persona.Confidence getConfidence() {
        return confidence;
    }

    public long getDateAadded() {
        return dateAdded;
    }

    public CentralRepoExaminer getExaminer() {
        return examiner;
    }

    public PersonaAlias(long id, long personaId, String alias, String justification, Persona.Confidence confidence, long dateAdded, CentralRepoExaminer examiner) {
        this.id = id;
        this.personaId = personaId;
        this.alias = alias;
        this.justification = justification;
        this.confidence = confidence;
        this.dateAdded = dateAdded;
        this.examiner = examiner;
    }

    /**
     * Creates an alias for the specified Persona.
     *
     * @param persona Persona for which the alias is being added.
     * @param alias Alias name.
     * @param justification Reason for assigning the alias, may be null.
     * @param confidence Confidence level.
     *
     * @return PersonaAlias
     * @throws CentralRepoException If there is an error in creating the alias.
     * @throws IllegalStateException If central repository is not enabled.
     */
    static PersonaAlias addPersonaAlias(Persona persona, String alias, String justification, Persona.Confidence confidence) throws CentralRepoException, IllegalStateException {

        if (!CentralRepository.isEnabled()) {
            throw new IllegalStateException("Central Repository is not enabled.");
        }

        CentralRepoExaminer examiner = CentralRepository.getInstance().getOrInsertExaminer(System.getProperty("user.name"));

        Instant instant = Instant.now();
        Long timeStampMillis = instant.toEpochMilli();

        String insertClause = " INTO persona_alias (persona_id, alias, justification, confidence_id, date_added, examiner_id ) "
                + "VALUES ( "
                + persona.getId() + ", "
                + "'" + alias + "', "
                + "'" + ((StringUtils.isBlank(justification) ? "" : SleuthkitCase.escapeSingleQuotes(justification))) + "', "
                + confidence.getLevelId() + ", "
                + timeStampMillis.toString() + ", "
                + examiner.getId()
                + ")";

        CentralRepository.getInstance().executeInsertSQL(insertClause);

        String queryClause = SELECT_QUERY_BASE
                + "WHERE pa.persona_id = " + persona.getId()
                + " AND pa.alias = \"" + alias + "\""
                + " AND pa.date_added = " + timeStampMillis
                + " AND pa.examiner_id = " + examiner.getId();

        PersonaAliasesQueryCallback queryCallback = new PersonaAliasesQueryCallback();
        CentralRepository.getInstance().executeSelectSQL(queryClause, queryCallback);

        Collection<PersonaAlias> aliases = queryCallback.getAliases();
        if (aliases.size() != 1) {
            throw new CentralRepoException("Alias add query failed");
        }

        return aliases.iterator().next();
    }

    /**
     * Removes a PersonaAlias.
     *
     * @param alias Alias to remove.
     *
     * @throws CentralRepoException If there is an error in removing the alias.
     * @throws IllegalStateException If central repository is not enabled.
     */
    static void removePersonaAlias(PersonaAlias alias) throws CentralRepoException, IllegalStateException {
        if (!CentralRepository.isEnabled()) {
            throw new IllegalStateException("Central Repository is not enabled.");
        }

        String deleteClause = " DELETE FROM persona_alias WHERE id = " + alias.getId();
        CentralRepository.getInstance().executeDeleteSQL(deleteClause);
    }

    /**
     * Modifies a PesronaAlias.
     *
     * @param alias Alias to modify.
     *
     * @throws CentralRepoException If there is an error in modifying the alias.
     * @throws IllegalStateException If central repository is not enabled.
     */
    static void modifyPersonaAlias(PersonaAlias alias, Persona.Confidence confidence, String justification) throws CentralRepoException, IllegalStateException {
        if (!CentralRepository.isEnabled()) {
            throw new IllegalStateException("Central Repository is not enabled.");
        }

        CentralRepository cr = CentralRepository.getInstance();
        String updateClause = "UPDATE persona_alias SET confidence_id = " + confidence.getLevelId() + ", justification = \"" + justification + "\" WHERE id = " + alias.id;
        cr.executeUpdateSQL(updateClause);
    }

    /**
     * Callback to process a Persona aliases query.
     */
    static class PersonaAliasesQueryCallback implements CentralRepositoryDbQueryCallback {

        private final Collection<PersonaAlias> personaAliases = new ArrayList<>();

        @Override
        public void process(ResultSet rs) throws SQLException {

            while (rs.next()) {
                CentralRepoExaminer examiner = new CentralRepoExaminer(
                        rs.getInt("examiner_id"),
                        rs.getString("login_name"));

                PersonaAlias alias = new PersonaAlias(
                        rs.getLong("id"),
                        rs.getLong("persona_id"),
                        rs.getString("alias"),
                        rs.getString("justification"),
                        Persona.Confidence.fromId(rs.getInt("confidence_id")),
                        Long.parseLong(rs.getString("date_added")),
                        examiner);

                personaAliases.add(alias);
            }
        }

        Collection<PersonaAlias> getAliases() {
            return Collections.unmodifiableCollection(personaAliases);
        }
    };

    /**
     * Gets all aliases for the persona with specified id.
     *
     * @param personaId Id of the persona for which to get the aliases.
     * @return A collection of aliases, may be empty.
     *
     * @throws CentralRepoException If there is an error in retrieving aliases.
     * @throws IllegalStateException If central repository is not enabled.
     */
    public static Collection<PersonaAlias> getPersonaAliases(long personaId) throws CentralRepoException, IllegalStateException {
        if (!CentralRepository.isEnabled()) {
            throw new IllegalStateException("Central Repository is not enabled.");
        }

        String queryClause = SELECT_QUERY_BASE + "WHERE pa.persona_id = " + personaId;

        PersonaAliasesQueryCallback queryCallback = new PersonaAliasesQueryCallback();
        CentralRepository.getInstance().executeSelectSQL(queryClause, queryCallback);

        return queryCallback.getAliases();
    }

}
