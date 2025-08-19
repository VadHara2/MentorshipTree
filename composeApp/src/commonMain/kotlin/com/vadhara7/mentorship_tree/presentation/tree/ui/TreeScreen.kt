package com.vadhara7.mentorship_tree.presentation.tree.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vadhara7.mentorship_tree.domain.model.ui.MentorshipTree
import com.vadhara7.mentorship_tree.domain.model.ui.RelationNode
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeIntent
import com.vadhara7.mentorship_tree.presentation.tree.vm.TreeState
import mentorshiptree.composeapp.generated.resources.Res
import mentorshiptree.composeapp.generated.resources.add_mentee
import mentorshiptree.composeapp.generated.resources.add_mentor
import mentorshiptree.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun TreeScreen(modifier: Modifier = Modifier, onIntent: (TreeIntent) -> Unit, state: TreeState) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Tree(
            modifier = Modifier.fillMaxSize(),
            centerLabel = state.userName,
            mentorshipTree = state.mentorshipTree,
            onAddMentorClick = {
                onIntent(TreeIntent.OnAddMentorClick)
            },
            onAddMenteeClick = {
                onIntent(TreeIntent.OnAddMenteeClick)
            },
            onDeleteNode = { node ->
                onIntent(TreeIntent.OnDeleteRelation(node))
            }
        )
    }
}


@Composable
fun Tree(
    modifier: Modifier = Modifier,
    centerLabel: String? = null,
    mentorshipTree: MentorshipTree,
    onAddMentorClick: () -> Unit = {},
    onAddMenteeClick: () -> Unit = {},
    onDeleteNode: (RelationNode) -> Unit = {},
) {
    // We measure everything in root coords, then convert to this Box coords
    var boxTopLeft by remember { mutableStateOf(Offset.Zero) }
    var centerOffset by remember(mentorshipTree) { mutableStateOf<Offset?>(null) }
    val nodeOffsets = remember(mentorshipTree) { mutableStateMapOf<String, Offset>() }
    val menteeLevels = remember(mentorshipTree) { computeLevels(mentorshipTree.mentees) }
    val mentorLevels = remember(mentorshipTree) { computeLevels(mentorshipTree.mentors) }
    val edges = remember(mentorshipTree) {
        collectEdgesFrom(mentorshipTree.mentees, null) + collectEdgesFrom(
            mentorshipTree.mentors,
            null
        )
    }

    val outlineColor = MaterialTheme.colorScheme.outline
//    val centerText = centerLabel ?: stringResource(Res.string.you)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .onGloballyPositioned { coords ->
                boxTopLeft = coords.boundsInRoot().topLeft
            }
    ) {
        // Connection lines (drawn behind nodes)
        Canvas(modifier = Modifier.matchParentSize()) {
            val c = centerOffset ?: return@Canvas
            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            val curve = 40.dp.toPx()
            val lineColor = outlineColor

            edges.forEach { (parentUid, childUid) ->
                val start = parentUid?.let { nodeOffsets[it] } ?: c
                val end = nodeOffsets[childUid]
                if (start != null && end != null) {
                    val sign = if (end.y < start.y) -1f else 1f
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        cubicTo(
                            start.x,
                            start.y + sign * curve,
                            end.x,
                            end.y - sign * curve,
                            end.x,
                            end.y
                        )
                    }
                    drawPath(path = path, color = lineColor, style = stroke)
                }
            }
        }

        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LevelRow(
                nodes = emptyList(),
                onMeasured = { _, _ -> },
                onDeleteNode = onDeleteNode,
                showAddChip = true,
                addLabel = stringResource(Res.string.add_mentee),
                onAddClick = onAddMenteeClick
            )

            // Mentees (all generations). Show first generation (closest to center) just above the center.
            for (i in menteeLevels.lastIndex downTo 0) {
                LevelRow(
                    nodes = menteeLevels[i],
//                    onNodeClick = onNodeClick,
                    onMeasured = { id, centerInRoot ->
                        nodeOffsets[id] = centerInRoot - boxTopLeft
                    },
                    onDeleteNode = onDeleteNode
                )
            }

            // Center — current user
            Box(
                modifier = Modifier.onGloballyPositioned { coords ->
                    centerOffset = coords.boundsInRoot().center - boxTopLeft
                }
            ) {
                ElevatedCard(shape = MaterialTheme.shapes.extraLarge) {
                    Text(
                        text = "centerText",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Mentors (all generations). Show first generation (closest to center) right below the center.
            for (i in 0..mentorLevels.lastIndex) {
                val isFirstRowBelow = (i == 0)
                LevelRow(
                    nodes = mentorLevels[i],
                    onMeasured = { id, centerInRoot ->
                        nodeOffsets[id] = centerInRoot - boxTopLeft
                    },
                    onDeleteNode = onDeleteNode,
                    showAddChip = isFirstRowBelow,
                    addLabel = stringResource(Res.string.add_mentor),
                    onAddClick = onAddMentorClick
                )
            }

            if (mentorLevels.isEmpty()) {
                LevelRow(
                    nodes = emptyList(),
                    onMeasured = { id, centerInRoot ->
                        nodeOffsets[id] = centerInRoot - boxTopLeft
                    },
                    onDeleteNode = onDeleteNode,
                    showAddChip = true,
                    addLabel = stringResource(Res.string.add_mentor),
                    onAddClick = onAddMentorClick
                )
            }
        }
    }
}

@Composable
private fun LevelRow(
    nodes: List<RelationNode>,
    onMeasured: (id: String, centerInRoot: Offset) -> Unit,
    onDeleteNode: (RelationNode) -> Unit = {},
    showAddChip: Boolean = false,
    addLabel: String = "",
    onAddClick: () -> Unit = {}
) {
    if (nodes.isEmpty() && !showAddChip) {
        Spacer(Modifier.height(0.dp))
        return
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        nodes.forEach { node ->
            PersonChip(
                node = node,
//                onClick = { onNodeClick(node) },
                onMeasured = { center -> onMeasured(node.user.uid, center) },
                onDelete = { onDeleteNode(node) }
            )
        }
        if (showAddChip) {
            AssistChip(
                onClick = onAddClick,
                label = { Text(addLabel) }
            )
        }
    }
}

@Composable
private fun PersonChip(
    node: RelationNode,
//    onClick: () -> Unit,
    onMeasured: (centerInRoot: Offset) -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box {
        AssistChip(
            onClick = {
                menuExpanded = true
            },
            label = {
                Text(
                    text = node.user.displayName ?: node.user.email,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    onMeasured(coords.boundsInRoot().center)
                }

        )
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.delete)) },
                onClick = {
                    menuExpanded = false
                    onDelete()
                }
            )
        }
    }
}


private fun computeLevels(roots: List<RelationNode>): List<List<RelationNode>> {
    val levels = mutableListOf<List<RelationNode>>()
    var current = roots
    while (current.isNotEmpty()) {
        levels += current
        current = current.flatMap { it.children }
    }
    return levels
}

/** parentUid == null означає, що з'єднання йде з центру */
private fun collectEdgesFrom(
    roots: List<RelationNode>,
    parentUid: String?
): List<Pair<String?, String>> {
    val result = mutableListOf<Pair<String?, String>>()
    fun dfs(n: RelationNode, parent: String?) {
        result += (parent to n.user.uid)
        n.children.forEach { dfs(it, n.user.uid) }
    }
    roots.forEach { dfs(it, parentUid) }
    return result
}