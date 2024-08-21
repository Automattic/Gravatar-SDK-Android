# frozen_string_literal: true

github.dismiss_out_of_range_messages

# `files: []` forces rubocop to scan all files, not just the ones modified in the PR
# `skip_bundle_exec` prevents RuboCop from running using `bundle exec`, which we don't want on the linter agent
rubocop.lint(files: [], force_exclusion: true, inline_comment: true, fail_on_inline_comment: true,
             include_cop_names: true, skip_bundle_exec: true)

manifest_pr_checker.check_gemfile_lock_updated
