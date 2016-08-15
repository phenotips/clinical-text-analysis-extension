/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/
 */
/**
 * Originally written by the Apache software foundation and distributed under
 * the following license:
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.phenotips.textanalysis.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides utilities for permutation algorithms.
 * Written by the Mayo Clinic for apache ctakes with minor adaptations to the getPermutationList
 * method.
 *
 * @version $Id$
 */
public final class PermutationUtil
{

    /**
     * Private CTOR.
     */
    private PermutationUtil()
    {
        throw new AssertionError();
    }

    /**
     * Gets all permutations for the given level and all sub-levels.
     *
     * @param maxLevel -
     * @return the permutations
     */
    public static List<List<Integer>> getPermutationList(final int maxLevel)
    {
        final List<List<Integer>> permList = new ArrayList<>();
        for (int levelIdx = maxLevel; levelIdx >= 0; levelIdx--) {
            // contains ALL index values
            final List<Integer> baseNumList = new ArrayList<>();
            for (int j = 0; j <= levelIdx; j++) {
                baseNumList.add(j);
            }

            final Collection<List<Integer>> numListCol = new ArrayList<>();
            if (levelIdx != maxLevel) {
                numListCol.addAll(getNumLists(maxLevel, baseNumList));
            } else {
                numListCol.add(baseNumList);
            }
            for (List<Integer> numList : numListCol) {
                final Collection<List<Integer>> pCol = PermutationUtil.getLinearPermutations(numList);
                for (List<Integer> permutation : pCol) {
                    permList.add(permutation);
                }
            }

            if (levelIdx == 0) {
                permList.add(new ArrayList<Integer>());
            }
        }

        return permList;
    }

    private static Collection<List<Integer>> getNumLists(final int maxLevel, final List<Integer> baseNumList)
    {
        final Collection<List<Integer>> numListCol = new ArrayList<>();
        buildPermutations(maxLevel, baseNumList, numListCol, new ArrayList<Integer>(), 0);
        filterNonIncreasingLists(numListCol);
        return numListCol;
    }

    /**
    * Filters the number lists such that only lists with increasing numbers are
    * kept.
    *
    * @param numListCol -
    */
    private static void filterNonIncreasingLists(final Collection<List<Integer>> numListCol)
    {
        final Set<List<Integer>> removalSet = new HashSet<>();
        for (List<Integer> numList : numListCol) {
            Integer largestNum = null;
            for (Integer num : numList) {
                if (largestNum == null) {
                    largestNum = num;
                } else {
                    final int comparison = largestNum.compareTo(num);
                    if (comparison == 1) {
                        removalSet.add(numList);
                    } else {
                        largestNum = num;
                    }
                }
            }
        }
        numListCol.removeAll(removalSet);
    }

    /**
     * Recursively builds permutations of numbers specified by the base num
     * list. This includes permutations of these numbers with few items than the
     * original list.
     *
     * @param maxLevel      -
     * @param baseNumList   -
     * @param numListCol    -
     * @param residualList  -
     * @param theResidualCount -
     */
    private static void buildPermutations(final int maxLevel, final List<Integer> baseNumList,
            final Collection<List<Integer>> numListCol,
            final List<Integer> residualList, int theResidualCount)
    {
        /* For checkstyle */
        int residualCount = theResidualCount;
        if (residualCount > baseNumList.size()) {
            return;
        } else if (residualCount == baseNumList.size()) {
            numListCol.add(new ArrayList<>(residualList));
            return;
        } else {
            final int num = baseNumList.get(residualCount);
            residualCount++;
            for (int i = num; i <= maxLevel; i++) {
                List<Integer> tempList = new ArrayList<>(residualList);
                if (!tempList.contains(i)) {
                    tempList.add(i);
                    buildPermutations(maxLevel, baseNumList, numListCol, tempList, residualCount);
                }
            }
        }
    }

    /**
     * Gets a collection of lists, each list represents a single permutation.
     * This permutation is composed of Integer objects in defined order.
     *
     * @param numList -
     * @return      -
     */
    public static Collection<List<Integer>> getLinearPermutations(final List<Integer> numList)
    {
        final Collection<List<Integer>> permutations = new ArrayList<>();
        getLinearPermutations(permutations, new ArrayList<Integer>(), numList);
        return permutations;
    }

    /**
     * Recursively builds permutations from the number list. The size of the permutations remains constant.
     *
     * @param permutations  -
     * @param plusList      -
     * @param numList       -
     */
    private static void getLinearPermutations(final Collection<List<Integer>> permutations,
            final List<Integer> plusList, final List<Integer> numList)
    {
        for (Integer num : numList) {
            final List<Integer> subList = new ArrayList<>(numList);
            //         subList.addAll(numList);
            subList.remove(num);

            plusList.add(num);

            if (!subList.isEmpty()) {
                getLinearPermutations(permutations, plusList, subList);
            } else {
                final List<Integer> permutation = new ArrayList<>(plusList);
                permutations.add(permutation);
            }
            plusList.remove(num);
        }
    }
}
