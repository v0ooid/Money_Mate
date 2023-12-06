package my.edu.tarc.moneymate.Alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.Goal.Goal
import my.edu.tarc.moneymate.R

class GoalAlarmAdapter(private var goals: List<Goal>, private val onGoalClick: (Goal) -> Unit) : RecyclerView.Adapter<GoalAlarmAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvGoalTitle)
        private val targetDateTextView: TextView = itemView.findViewById(R.id.goal_targetDate)
        private val savedAmountTextView: TextView = itemView.findViewById(R.id.goalSavedMoney)
        private val targetAmountTextView: TextView = itemView.findViewById(R.id.goal_targetAmount)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress)

        fun bind(goal: Goal, onGoalClick: (Goal) -> Unit) {
            titleTextView.text = goal.title
            targetDateTextView.text = "Target Date: ${goal.desiredDate}"
            savedAmountTextView.text = "Saved: RM ${goal.savedAmount}"
            targetAmountTextView.text = "Goal: RM ${goal.targetAmount}"

            val progress = if (goal.targetAmount > 0) {
                (goal.savedAmount.toFloat() / goal.targetAmount * 100).toInt()
            } else {
                0
            }
            progressBar.progress = progress
            itemView.setOnClickListener { onGoalClick(goal) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item_layout, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal, onGoalClick)
        holder.itemView.setOnClickListener { onGoalClick(goal) }
    }

    override fun getItemCount() = goals.size

    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}

